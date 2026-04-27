# WearOS センシングライブラリ 設計方針

## 目標

このプロジェクトを「別のアプリにコピペ or ライブラリとして追加するだけで使える」センシング基盤にする。
そのために、**センシング・保存・送信の 3 責務を完全に分離**し、それぞれを独立したモジュールとして設計する。

---

## 現状の問題点

| 問題 | 箇所 |
|------|------|
| `BaseSensorService` が `UdpSender` を直接 `new` している（密結合） | `BaseSensorService.kt:19` |
| IP アドレスがソースコードに直書き | `BaseSensorService.kt`, `MainActivity.kt` |
| センサー → ネットワークが直結で、保存層がない | `onSensorChanged()` |
| `saveToCSV()` が未実装のまま残っている | `BaseSensorService.kt:48` |
| `formatMessage()` が CSV 文字列を返すだけで型がない | 各センサー Service |

---

## 3 モジュール構成

```
┌──────────────────────────────────────────────────────┐
│                    アプリ側 (利用者)                   │
│  SensorPipeline を組み立てて start() を呼ぶだけ       │
└──────────┬───────────────────────┬───────────────────┘
           │                       │
    ┌──────▼──────┐         ┌──────▼──────┐
    │  SensorData │  (型)   │SensorPipeline│ (配線)
    └──────┬──────┘         └──────┬──────┘
           │                       │
    ┌──────▼──────┐   ┌────────────▼──────────────┐
    │   sensing   │   │  storage  │    network     │
    │  (取得)     │──▶│  (保存)   │─▶  (送信)     │
    └─────────────┘   └───────────┴───────────────┘
```

### モジュール 1: `sensing` — センサーデータ取得

**責務**: センサーから値を読み取り、型付きの `SensorData` として通知する。ネットワークや保存は知らない。

```
sensing/
├── SensorData.kt           # データクラス (type, values, timestampNs)
├── SensorCollectorListener.kt  # コールバックインターフェース
├── BaseSensorCollector.kt  # 抽象基底: SensorManager 登録/解除
├── AccelerometerCollector.kt
├── HeartRateCollector.kt
└── LightCollector.kt
```

**インターフェース**:

```kotlin
data class SensorData(
    val type: String,           // "accelerometer" / "heart_rate" / "light"
    val values: FloatArray,
    val timestampNs: Long
)

interface SensorCollectorListener {
    fun onSensorData(data: SensorData)
}
```

利用者は `listener` を渡すだけ。送信先も保存先も sensing 層は知らない。

---

### モジュール 2: `storage` — JSON ローカル保存

**責務**: `SensorData` を JSON にシリアライズしてファイルに書き込む。取得方法も送信方法も知らない。

```
storage/
├── SensorDataSerializer.kt   # インターフェース: serialize(data): String
├── JsonSerializer.kt         # 実装: JSON 文字列を返す
├── SensorDataStore.kt        # インターフェース: save(json: String)
└── LocalFileStore.kt         # 実装: アプリ内ストレージへ追記
```

**JSON 形式** (例):

```json
{
  "type": "accelerometer",
  "values": [0.12, -9.80, 0.05],
  "timestamp_ns": 123456789
}
```

**インターフェース**:

```kotlin
interface SensorDataSerializer {
    fun serialize(data: SensorData): String
}

interface SensorDataStore {
    fun save(serialized: String)
    fun readAll(): List<String>
    fun clear()
}
```

---

### モジュール 3: `network` — データ送信

**責務**: 文字列を外部へ送る。センサーも保存も知らない。

```
network/
├── DataSender.kt      # インターフェース: send(payload: String)
├── UdpSender.kt       # 実装: UDP 送信
└── (将来) HttpSender.kt / MqttSender.kt
```

**インターフェース**:

```kotlin
interface DataSender {
    fun send(payload: String)
}
```

既存の `BaseSender` / `UdpSender` をこの設計に合わせてリネーム・整理する。

---

### 配線層: `pipeline` — モジュールを繋ぐ

**責務**: sensing → storage → network を組み合わせる。アプリ側が差し替え可能な構成を渡す。

```
pipeline/
├── SensorPipelineConfig.kt  # 設定 data class
└── SensorPipeline.kt        # 組み立てと start/stop
```

```kotlin
data class SensorPipelineConfig(
    val collectors: List<BaseSensorCollector>,
    val serializer: SensorDataSerializer = JsonSerializer(),
    val store: SensorDataStore? = null,       // null なら保存スキップ
    val sender: DataSender? = null,           // null なら送信スキップ
)

class SensorPipeline(val config: SensorPipelineConfig) {
    fun start() { /* collectors を起動し listener を配線 */ }
    fun stop()  { /* collectors を停止 */ }
}
```

アプリ側の最小コード例:

```kotlin
val pipeline = SensorPipeline(
    SensorPipelineConfig(
        collectors = listOf(AccelerometerCollector(context), HeartRateCollector(context)),
        sender = UdpSender("192.168.1.100", 6666)
    )
)
pipeline.start()
```

---

## Android Service との関係

Wear OS では Service が必要なケースがある。Service は UI 層の責務なのでライブラリ本体には含めない。
アプリ側で薄いラッパー Service を書いて Pipeline を内部で使う形にする。

```kotlin
class MySensingService : Service() {
    private lateinit var pipeline: SensorPipeline
    override fun onStartCommand(...): Int {
        pipeline = SensorPipeline(/* 設定 */)
        pipeline.start()
        return START_STICKY
    }
    override fun onDestroy() { pipeline.stop() }
}
```

---

## 依存関係のルール

```
sensing   ───▶ (なし)        センサー値の取得だけ
storage   ───▶ sensing       SensorData の型だけ参照
network   ───▶ (なし)        文字列を送るだけ
pipeline  ───▶ 全モジュール   唯一の配線場所
app       ───▶ pipeline のみ  モジュール内部を直接触らない
```

- 上位レイヤーは下位の実装クラスを直接 import しない
- 差し替えはすべてインターフェース経由

---

## 今後の実装順

1. `SensorData` 型の定義と `SensorCollectorListener` インターフェース
2. `BaseSensorCollector` + 各センサー実装を Service から分離
3. `JsonSerializer` + `LocalFileStore` の実装
4. `DataSender` インターフェースへ `UdpSender` を移行
5. `SensorPipeline` で全体を配線
6. 既存の `BaseSensorService` を Pipeline を使う薄い Service に置き換え
7. (任意) Android Library モジュール (`.aar`) としてビルド設定を追加

---

## Android Library としてパッケージする場合

将来的には Gradle マルチモジュール構成で `.aar` 化する。

```
wearos-sensing-lib/
├── sensing/          ← Android Library module
├── storage/          ← Android Library module
├── network/          ← Android Library module
├── pipeline/         ← Android Library module
└── app/              ← サンプルアプリ (現在の app/)
```

それまでは `sensing/`, `storage/`, `network/`, `pipeline/` フォルダ単位でのコピペ運用を想定する。
