# 使い方ガイド

## インストール

### JitPack 経由（推奨）

**Step 1** — `settings.gradle.kts` に JitPack リポジトリを追加:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2** — `app/build.gradle.kts` に依存を追加:

```kotlin
dependencies {
    implementation("com.github.haruu11113.wearos-sensor-kit:pipeline:1.0.1")
}
```

`pipeline` モジュールが `sensing` / `storage` / `network` を `api` で再エクスポートしているため、
`pipeline` への依存を 1 つ追加するだけで全モジュールが使えます。

**Step 3** — `AndroidManifest.xml` にパーミッションを追加:

```xml
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.BODY_SENSORS_BACKGROUND" />
<!-- WearOS 4 (API 34+) で HeartRateCollector を使う場合は追加 -->
<uses-permission android:name="android.permission.health.READ_HEART_RATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

---

## 実行時パーミッションのリクエスト

マニフェストへの宣言に加え、`BODY_SENSORS`（および API 34 以降では `health.READ_HEART_RATE`）は実行時にもリクエストが必要です。

```kotlin
import android.Manifest
import android.os.Build

val permissions = buildList {
    add(Manifest.permission.BODY_SENSORS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        add("android.permission.health.READ_HEART_RATE")
    }
}.toTypedArray()

// ActivityResultContracts.RequestMultiplePermissions などで上記をリクエストしてください
```

---

## 利用可能な Collector 一覧

| クラス | センサ | JSON `type` 値 | 取得できる値 | 必要なパーミッション |
|--------|--------|---------------|-------------|-------------------|
| `AccelerometerCollector` | 加速度センサ | `"accelerometer"` | X/Y/Z 加速度 (m/s²) | - |
| `GyroscopeCollector` | ジャイロスコープ | `"gyroscope"` | X/Y/Z 角速度 (rad/s) | - |
| `MagneticFieldCollector` | 地磁気センサ | `"magnetic_field"` | X/Y/Z 磁束密度 (μT) | - |
| `RotationVectorCollector` | 回転ベクトル | `"rotation_vector"` | クォータニオン | - |
| `GravityCollector` | 重力センサ | `"gravity"` | X/Y/Z 重力成分 (m/s²) | - |
| `LinearAccelerationCollector` | 線形加速度 | `"linear_acceleration"` | X/Y/Z 加速度（重力除去済み） | - |
| `StepCounterCollector` | 歩数カウンター | `"step_counter"` | 累積歩数 | - |
| `StepDetectorCollector` | 歩行検出 | `"step_detector"` | 1歩ごとにイベント (1.0) | - |
| `PressureCollector` | 気圧センサ | `"pressure"` | 大気圧 (hPa) | - |
| `HeartRateCollector` | 心拍数 | `"heart_rate"` | bpm | `BODY_SENSORS` |
| `HeartBeatCollector` | 心拍ビート | `"heart_beat"` | 信頼度 (0〜1) ※RRI 算出用 | `BODY_SENSORS` |
| `OxygenSaturationCollector` | SpO2 | `"oxygen_saturation"` | 血中酸素飽和度 (%) | `BODY_SENSORS` |
| `SkinTemperatureCollector` | 皮膚温度 | `"skin_temperature"` | 皮膚表面温度 (℃) ※Pixel Watch 2 | `BODY_SENSORS` |
| `LightCollector` | 照度センサ | `"light"` | 照度 (lux) | - |
| `OffBodyDetectCollector` | 装着検出 | `"off_body_detect"` | 0.0=装着中 / 1.0=非装着 | - |

JSON フォーマット例（`UdpSender` / `HttpSender` 送信時）:

```json
{"type":"accelerometer","values":[0.12,-9.80,0.05],"timestamp_ns":123456789}
```

---

## コード例

### 最小構成（加速度 + UDP 送信）

```kotlin
val factory = SensorPipelineFactory(context)

val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this)),
    consumers  = listOf(factory.senderConsumer(UdpSender("192.168.1.100", 6666)))
)
pipeline.start()
```

### 複数センサーを使う

```kotlin
val factory = SensorPipelineFactory(context)

val pipeline = factory.buildPipeline(
    collectors = listOf(
        AccelerometerCollector(this),
        GyroscopeCollector(this),
        HeartRateCollector(this)
    ),
    consumers = listOf(factory.senderConsumer(UdpSender("192.168.1.100", 6666)))
)
pipeline.start()
```

### Store に蓄積 → あとで送信（Store-and-Forward）

ネット未接続時や送信を遅延させたい場合に使うパターン。
リアルタイム収集と送信を分離できます。

```kotlin
val factory = SensorPipelineFactory(context)

// 収集：常時 Store に保存
val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this)),
    consumers  = listOf(factory.storeConsumer())
)
pipeline.start()

// 送信：任意のタイミングで呼ぶ（ネット接続時、WorkManager など）
val syncJob = factory.buildSyncJob(UdpSender("192.168.1.100", 6666))
syncJob.execute()
```

### HTTP で送信する

```kotlin
val factory = SensorPipelineFactory(context)
val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this)),
    consumers  = listOf(factory.senderConsumer(HttpSender("https://example.com/api/sensor")))
)
```

- `Content-Type: application/json` で POST します
- タイムアウトは接続・読み取りともに 5000ms
- 非 2xx レスポンスは `Log.w` で警告、例外は `Log.e` で記録してクラッシュしません

### Cloud Firestore に送信する

```kotlin
val factory = SensorPipelineFactory(context)
val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this)),
    consumers  = listOf(factory.storeConsumer())
)
pipeline.start()

// 任意のタイミングで Firestore へ転送
factory.buildSyncJob(FirestoreSender()).execute()
```

#### Cloud Firestore の事前準備（利用側アプリ）

**ステップ 1: Firebase プロジェクトを作成し `google-services.json` を配置する**

1. [Firebase Console](https://console.firebase.google.com/) で Firebase プロジェクトを作成する
2. 「Android アプリを追加」からアプリを登録し、`google-services.json` をダウンロードする
3. ダウンロードした `google-services.json` を **利用側アプリの `app/` ディレクトリ直下** に配置する

> **注意**: `google-services.json` には Firebase プロジェクトの認証情報が含まれます。
> リポジトリにコミットしないよう、利用側アプリの `.gitignore` に追加してください。

```
# .gitignore
app/google-services.json
```

CI/CD（GitHub Actions）での注入例:

```yaml
- name: Write google-services.json
  run: echo "${{ secrets.GOOGLE_SERVICES_JSON }}" > app/google-services.json
```

**ステップ 2: Google Services プラグインを追加する**

```kotlin
// build.gradle.kts (root)
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}

// app/build.gradle.kts
plugins {
    id("com.google.gms.google-services")
}
```

**ステップ 3: Firestore のセキュリティルールを設定する**

Firebase Console の「Firestore Database」→「ルール」で書き込みを許可するルールを設定してください。
開発中は以下のルールで動作確認できますが、本番環境では適切に制限してください。

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;  // 開発用。本番では適切に制限すること
    }
  }
}
```

#### Firestore のドキュメント構造

```
sensor_data/
  {auto-id}/
    type:             "accelerometer"
    values:           [0.12, -9.80, 0.05]
    timestamp_ns:     123456789
    server_timestamp: <サーバータイムスタンプ>
```

---

### 独自 Consumer の実装例

`SensorConsumer` を実装すれば MQTT・WebSocket・ML 推論など任意の処理を追加できます。

```kotlin
class MyConsumer : SensorConsumer {
    override fun onData(data: SensorData) {
        // 好きな処理（例: スライディングウィンドウで ML 推論）
    }
}

val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this)),
    consumers  = listOf(
        factory.storeConsumer(),
        MyConsumer()
    )
)
```

---

### Service でのライフサイクル管理

`pipeline.stop()` 呼び出し時にバッファの残データをフラッシュします。`stop()` を呼ばないとデータが失われる可能性があります。

```kotlin
class MySensingService : Service() {
    private lateinit var pipeline: SensorPipeline

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val factory = SensorPipelineFactory(this)
        pipeline = factory.buildPipeline(
            collectors = listOf(AccelerometerCollector(this), HeartRateCollector(this)),
            consumers  = listOf(factory.senderConsumer(UdpSender("192.168.1.100", 6666)))
        )
        pipeline.start()
        return START_STICKY
    }

    override fun onDestroy() {
        pipeline.stop()    // 必ず呼ぶ
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
```

---

## 代替インストール方法: Git Submodule

JitPack が使えない・最新の main を直接使いたい場合は Git Submodule での組み込みも可能です。

```bash
git submodule add https://github.com/haruu11113/wearos-sensor-kit.git libs/wearos-sensor-kit
git submodule update --init
```

`local.properties` をシンボリックリンクで共有:

```bash
ln -s $(pwd)/local.properties libs/wearos-sensor-kit/local.properties
```

`settings.gradle.kts` に追記:

```kotlin
includeBuild("libs/wearos-sensor-kit") {
    dependencySubstitution {
        substitute(module("com.github.haruu11113.wearos-sensor-kit:pipeline")).using(project(":pipeline"))
    }
}
```

更新時は:

```bash
cd libs/wearos-sensor-kit
git pull origin main
cd ../..
git add libs/wearos-sensor-kit
git commit -m "chore: update wearos-sensor-kit submodule"
```
