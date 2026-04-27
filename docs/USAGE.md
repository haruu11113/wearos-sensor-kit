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

### 保存だけ（送信しない）

```kotlin
SensorPipelineConfig(
    collectors = listOf(AccelerometerCollector(this)),
    store = LocalFileStore(this)
    // sender を省略 → 送信スキップ
)
```

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

### 独自の送信先に差し替える

```kotlin
class HttpSender(private val url: String) : DataSender {
    override fun send(payload: String) {
        // HTTP POST など
    }
}

SensorPipelineConfig(
    collectors = listOf(AccelerometerCollector(this)),
    sender = HttpSender("https://example.com/sensor")
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
