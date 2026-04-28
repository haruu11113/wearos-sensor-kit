# 使い方ガイド

## Git Submodule で別プロジェクトに組み込む（現在の推奨方法）

### 1. サブモジュールとして追加

利用側プロジェクトのルートで実行:

```bash
git submodule add https://github.com/haruu11113/wearos.git libs/wearos
git submodule update --init
```

ディレクトリ構成:

```
your-app/
├── app/
├── libs/
│   └── wearos/          ← このリポジトリ
│       ├── sensing/
│       ├── storage/
│       ├── network/
│       └── pipeline/
└── settings.gradle.kts
```

### 2. local.properties をシンボリックリンクで共有

composite build では `libs/wearos/` にも Android SDK パスが必要です。
親プロジェクトのルートで以下を実行し、`local.properties` へのシンボリックリンクを作成してください。

```bash
ln -s $(pwd)/local.properties libs/wearos/local.properties
```

### 3. settings.gradle.kts に追記

```kotlin
// your-app/settings.gradle.kts
includeBuild("libs/wearos") {
    dependencySubstitution {
        substitute(module("com.github.haruu11113.wearos:pipeline")).using(project(":pipeline"))
    }
}
```

`pipeline` モジュールが `sensing` / `storage` / `network` を `api` で再エクスポートしているため、
`pipeline` への依存を 1 つ追加するだけで全モジュールが使えます。

### 4. app/build.gradle.kts に依存を追加

```kotlin
// your-app/app/build.gradle.kts
dependencies {
    implementation("com.github.haruu11113.wearos:pipeline:1.0.0-SNAPSHOT")
}
```

### 5. AndroidManifest.xml にパーミッションを追加

```xml
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.BODY_SENSORS_BACKGROUND" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

---

## コード例

### 最小構成（加速度 + UDP 送信）

```kotlin
import com.example.wearos.pipeline.SensorPipeline
import com.example.wearos.pipeline.SensorPipelineConfig
import com.example.wearos.sensing.AccelerometerCollector
import com.example.wearos.network.UdpSender

class MySensingService : Service() {
    private lateinit var pipeline: SensorPipeline

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        pipeline = SensorPipeline(
            SensorPipelineConfig(
                collectors = listOf(AccelerometerCollector(this)),
                sender = UdpSender("192.168.1.100", 6666)
            )
        )
        pipeline.start()
        return START_STICKY
    }

    override fun onDestroy() {
        pipeline.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
```

### 全センサー + ローカル保存 + UDP 送信

```kotlin
pipeline = SensorPipeline(
    SensorPipelineConfig(
        collectors = listOf(
            AccelerometerCollector(this),
            HeartRateCollector(this),   // 要 BODY_SENSORS パーミッション
            LightCollector(this)
        ),
        store  = LocalFileStore(this),              // JSON をローカルに保存
        sender = UdpSender("192.168.1.100", 6666)  // UDP で送信
    )
)
```

### ローカルファイルに保存する（送信しない）

```kotlin
SensorPipelineConfig(
    collectors = listOf(AccelerometerCollector(this)),
    store = LocalFileStore(this)
    // sender を省略 → 送信スキップ
)
```

### Cloud Firestore に保存する

#### 事前準備（利用側アプリ）

1. [Firebase Console](https://console.firebase.google.com/) でプロジェクトを作成し、`google-services.json` をアプリモジュールに配置する
2. アプリの `build.gradle.kts` に Google Services プラグインを追加する:

```kotlin
// app/build.gradle.kts
plugins {
    id("com.google.gms.google-services")
}
```

3. ルートの `build.gradle.kts` にもプラグインを追加する:

```kotlin
// build.gradle.kts (root)
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

#### 使い方

```kotlin
SensorPipelineConfig(
    collectors = listOf(
        AccelerometerCollector(this),
        HeartRateCollector(this)
    ),
    store = FirestoreStore(collection = "sensor_data")
)
```

コレクション名は省略可能（デフォルト: `"sensor_data"`）。

#### Firestore のドキュメント構造

```
sensor_data/
  {auto-id}/
    type:             "accelerometer"
    values:           [0.12, -9.80, 0.05]
    timestamp_ns:     123456789
    server_timestamp: <サーバータイムスタンプ>
```

#### 注意事項

- `readAll()` / `clear()` は Firestore の非同期 API の性質上サポートしていません（呼ぶと `UnsupportedOperationException`）
- データの参照・削除は Firebase Console または Admin SDK を使ってください
- Firestore のセキュリティルールは Firebase Console で設定してください

### 独自のシリアライザーに差し替える

```kotlin
class CsvSerializer : SensorDataSerializer {
    override fun serialize(data: SensorData): String =
        "${data.type},${data.values.joinToString(",")},${data.timestampNs}"
}

SensorPipelineConfig(
    collectors = listOf(AccelerometerCollector(this)),
    serializer = CsvSerializer(),
    sender = UdpSender("192.168.1.100", 6666)
)
```

### HTTP で送信する

`HttpSender` はライブラリに組み込み済みです。URL を渡すだけで使えます。

```kotlin
SensorPipelineConfig(
    collectors = listOf(AccelerometerCollector(this)),
    sender = HttpSender("https://example.com/api/sensor")
)
```

- `Content-Type: application/json` で POST します
- タイムアウトは接続・読み取りともに 5000ms
- 非 2xx レスポンスは `Log.w` で警告、例外は `Log.e` で記録してクラッシュしません

### 独自の送信先に差し替える

`DataSender` を実装すれば MQTT・WebSocket など任意のプロトコルに対応できます。

```kotlin
class MqttSender(private val topic: String) : DataSender {
    override fun send(payload: String) {
        // MQTT publish など
    }
}

SensorPipelineConfig(
    collectors = listOf(AccelerometerCollector(this)),
    sender = MqttSender("sensors/accelerometer")
)
```

---

## サブモジュールの更新

ライブラリ側が更新されたときは利用側で以下を実行:

```bash
cd libs/wearos
git pull origin main
cd ../..
git add libs/wearos
git commit -m "chore: update wearos submodule"
```

---

## 将来: Gradle 依存（.aar / JitPack）での使用

JitPack 対応後は git submodule 不要になり、以下の記述だけで使えます。

```kotlin
// settings.gradle.kts
repositories {
    maven { url = uri("https://jitpack.io") }
}

// app/build.gradle.kts
dependencies {
    implementation("com.github.haruu11113.wearos:pipeline:1.0.0")
}
```
