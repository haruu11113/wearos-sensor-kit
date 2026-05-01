# WearOS センシングライブラリ

Wear OS デバイス（スマートウォッチ）でセンサーデータを取得し、JSON 形式でローカル保存・UDP 送信できるライブラリです。
センシング・保存・送信が疎結合に分離されており、別のアプリへのコピペや依存追加で再利用できます。

## 概要

| 項目 | 内容 |
|------|------|
| プラットフォーム | Wear OS (minSdk 30 / targetSdk 33) |
| 言語 | Kotlin |
| UI フレームワーク | Jetpack Compose (Wear Material) |
| 通信プロトコル | UDP |
| データ形式 | JSON (JSONL) |

## モジュール構成

```
wearos/
├── sensing/    センサーデータ取得（Android フレームワークのみ依存）
├── storage/    JSON シリアライズ + ローカル保存（sensing に依存）
├── network/    UDP 送信（依存なし）
├── pipeline/   3 モジュールを繋ぐ配線層（全モジュールに依存）
└── app/        サンプルアプリ（pipeline にのみ依存）
```

### sensing

| ファイル | 役割 |
|---------|------|
| `SensorData.kt` | センサー値の型 (`type`, `values`, `timestampNs`) |
| `SensorCollectorListener.kt` | データ受け取りコールバック |
| `BaseSensorCollector.kt` | SensorManager 登録・解除の抽象基底 |
| `AccelerometerCollector.kt` | 加速度センサー (`SENSOR_DELAY_GAME`) |
| `HeartRateCollector.kt` | 心拍数センサー (`SENSOR_DELAY_GAME`、要 `BODY_SENSORS` / `health.READ_HEART_RATE`（API 34+）) |
| `LightCollector.kt` | 照度センサー (`SENSOR_DELAY_NORMAL`) |

### storage

| ファイル | 役割 |
|---------|------|
| `SensorDataStore.kt` | 永続化インターフェース (`save` / `readAll` / `delete`) |
| `LocalFileStore.kt` | アプリ内ストレージへの JSONL 追記保存 |
| `SQLiteStore.kt` | SQLite による保存（store-and-forward 向け、デフォルト実装） |

### network

| ファイル | 役割 |
|---------|------|
| `DataSender.kt` | 送信インターフェース |
| `UdpSender.kt` | UDP 送信実装 |
| `HttpSender.kt` | HTTP POST 送信実装 |
| `FirestoreSender.kt` | Cloud Firestore 送信実装（バッチ書き込み対応） |

### pipeline

| ファイル | 役割 |
|---------|------|
| `SensorDataSerializer.kt` | シリアライズインターフェース |
| `JsonSerializer.kt` | `SensorData` → JSON 文字列 |
| `SensorConsumer.kt` | データ消費インターフェース (`onData` / `onStop`) |
| `StoreConsumer.kt` | `SensorConsumer` → `SensorDataStore` へ保存 |
| `SenderConsumer.kt` | `SensorConsumer` → `DataSender` へ送信 |
| `SensorPipeline.kt` | `start()` / `stop()` でフロー全体を制御 |
| `SyncJob.kt` | Store に蓄積済みデータを Sender へ一括転送（store-and-forward） |
| `SensorPipelineFactory.kt` | Pipeline / SyncJob を組み立てるファクトリ |

## データ形式

センサーイベントは以下の JSON 形式でシリアライズされます。

```json
{
  "type": "accelerometer",
  "values": [0.12, -9.80, 0.05],
  "timestamp_ns": 123456789
}
```

`type` の値:

| センサー | `type` 値 |
|---------|-----------|
| 加速度計 | `"accelerometer"` |
| 心拍数 | `"heart_rate"` |
| 照度 | `"light"` |

## インストール

### Git Submodule（現在の推奨方法）

**Step 1** — サブモジュールとして追加:

```bash
git submodule add https://github.com/haruu11113/wearos.git libs/wearos
git submodule update --init
```

**Step 2** — `local.properties` をシンボリックリンクで共有:

composite build では `libs/wearos/` にも Android SDK パスが必要です。
親プロジェクトの `local.properties` へのシンボリックリンクを作成して共有してください。

```bash
ln -s $(pwd)/local.properties libs/wearos/local.properties
```

**Step 3** — `settings.gradle.kts` に追記:

```kotlin
includeBuild("libs/wearos") {
    dependencySubstitution {
        substitute(module("com.github.haruu11113.wearos:pipeline")).using(project(":pipeline"))
    }
}
```

**Step 4** — `app/build.gradle.kts` に依存を追加:

```kotlin
dependencies {
    implementation("com.github.haruu11113.wearos:pipeline:1.0.0-SNAPSHOT")
}
```

`pipeline` が `sensing` / `storage` / `network` を再エクスポートしているため、この 1 行で全モジュールが使えます。

詳細・他のインストール方法 → [docs/USAGE.md](docs/USAGE.md)

## 使い方

### 最小コード例（送信のみ）

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

    override fun onDestroy() { pipeline.stop() }
    override fun onBind(intent: Intent?) = null
}
```

### Store に蓄積 → あとで送信（store-and-forward）

```kotlin
val factory = SensorPipelineFactory(context)

// 収集：常時 Store に保存
val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this)),
    consumers  = listOf(factory.storeConsumer())
)
pipeline.start()

// 送信：任意のタイミングで呼ぶ
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

利用側アプリに `google-services.json` の配置と Google Services プラグインの設定が必要です。
詳細セットアップ手順 → [docs/USAGE.md](docs/USAGE.md)

コード例の詳細 → [docs/USAGE.md](docs/USAGE.md)

## パーミッション

| パーミッション | 用途 |
|----------------|------|
| `BODY_SENSORS` | 心拍数センサーへのアクセス（実行時リクエスト） |
| `BODY_SENSORS_BACKGROUND` | バックグラウンドでの心拍数取得 |
| `health.READ_HEART_RATE` | 心拍数センサーへのアクセス（API 34 以降、実行時リクエスト） |
| `INTERNET` | UDP 送信 |
| `WAKE_LOCK` | センシング中のスリープ抑制 |

## サンプルアプリのビルド・実行

1. Android Studio でプロジェクトを開く
2. Wear OS 実機またはエミュレーターを選択
3. Run ▶ で `app` モジュールをインストール
4. アプリ起動 → パーミッション許可 → **Start** をタップ

UDP 受信の確認:

```bash
nc -ulp 6666
```

## アーキテクチャ

```
MainActivity
  └─ Start/Stop ──▶ SensingService
                      └─ SensorPipelineFactory
                           └─ SensorPipeline
                                ├─ AccelerometerCollector
                                ├─ HeartRateCollector      } onSensorChanged()
                                └─ LightCollector
                                     └─ SensorConsumer.onData()
                                          ├─ StoreConsumer ──▶ SQLiteStore（蓄積）
                                          └─ SenderConsumer ──▶ UdpSender など（リアルタイム送信）

SyncJob（任意タイミング）
  └─ SQLiteStore.readAll() ──▶ DataSender.send() ──▶ 送信完了後 SQLiteStore.delete()
```

## ドキュメント

- [使い方ガイド（Git Submodule / コード例）](docs/USAGE.md)
- [設計方針・モジュール詳細](docs/README.md)
- [実装 issue 一覧](docs/TODO/README.md)
