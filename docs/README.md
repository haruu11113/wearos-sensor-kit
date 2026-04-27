# WearOS センシングライブラリ 設計ドキュメント

## 目標

「別のアプリにコピペ or Gradle 依存として追加するだけで使える」センシング基盤。
センシング・保存・送信の 3 責務を完全に分離し、それぞれを独立したモジュールとして実装する。

---

## モジュール構成と依存関係

```
sensing   ───▶ (なし)        センサー値の取得だけ
storage   ───▶ sensing       SensorData の型だけ参照
network   ───▶ (なし)        文字列を送るだけ
pipeline  ───▶ 全モジュール   唯一の配線場所
app       ───▶ pipeline のみ  モジュール内部を直接触らない
```

上位レイヤーは下位の実装クラスを直接 import しない。差し替えはすべてインターフェース経由。

---

## モジュール詳細

### sensing — センサーデータ取得

**責務**: センサーから値を読み取り、型付きの `SensorData` として通知する。ネットワークや保存は知らない。

```
sensing/src/main/java/com/example/wearos/sensing/
├── SensorData.kt              データクラス (type, values, timestampNs)
├── SensorCollectorListener.kt コールバックインターフェース
├── BaseSensorCollector.kt     抽象基底: SensorManager 登録/解除
├── AccelerometerCollector.kt  加速度センサー
├── HeartRateCollector.kt      心拍数センサー（要 BODY_SENSORS）
└── LightCollector.kt          照度センサー
```

```kotlin
data class SensorData(
    val type: String,        // TYPE_ACCELEROMETER / TYPE_HEART_RATE / TYPE_LIGHT
    val values: FloatArray,
    val timestampNs: Long
)

interface SensorCollectorListener {
    fun onSensorData(data: SensorData)
}
```

`BaseSensorCollector.start(listener)` で登録、`stop()` で解除。
サブクラスは `sensorType`、`samplingRate`、`formatData()` を実装するだけでよい。

---

### storage — JSON ローカル保存

**責務**: `SensorData` を JSON にシリアライズしてファイルに書き込む。取得方法も送信方法も知らない。

```
storage/src/main/java/com/example/wearos/storage/
├── SensorDataSerializer.kt  インターフェース: serialize(data): String
├── JsonSerializer.kt        実装: JSON 文字列を返す（org.json 使用）
├── SensorDataStore.kt       インターフェース: save / readAll / clear
└── LocalFileStore.kt        実装: アプリ内ストレージへ JSONL 追記
```

出力 JSON:

```json
{
  "type": "accelerometer",
  "values": [0.12, -9.80, 0.05],
  "timestamp_ns": 123456789
}
```

`LocalFileStore` は 1 レコード = 1 行の JSONL 形式で保存する。

---

### network — データ送信

**責務**: 文字列ペイロードを外部へ送る。センサーも保存も知らない。

```
network/src/main/java/com/example/wearos/network/
├── DataSender.kt   インターフェース: send(payload: String)
└── UdpSender.kt    実装: UDP 送信（DatagramSocket）
```

`UdpSender` はコンストラクタで `address` と `port` を受け取る。IP のハードコードなし。
将来的に `HttpSender` や `MqttSender` を追加しても `DataSender` を実装するだけでよい。

---

### pipeline — 配線層

**責務**: sensing → storage → network を組み合わせる。アプリ側が差し替え可能な構成を渡す。

```
pipeline/src/main/java/com/example/wearos/pipeline/
├── SensorPipelineConfig.kt  設定 data class
└── SensorPipeline.kt        start() / stop() でフロー全体を制御
```

```kotlin
data class SensorPipelineConfig(
    val collectors: List<BaseSensorCollector>,
    val serializer: SensorDataSerializer = JsonSerializer(),
    val store: SensorDataStore? = null,   // null なら保存スキップ
    val sender: DataSender? = null        // null なら送信スキップ
)
```

`SensorPipeline` 内部では送信を `Executors.newSingleThreadExecutor()` でバックグラウンド実行する。

---

### app — サンプルアプリ

`SensingService` が `SensorPipeline` を生成・保持する薄いラッパー Service。
Intent の extras で UDP 送信先を渡す設計にしてあるため、送信先をアプリ外から変更できる。

```kotlin
// MainActivity.kt
context.startService(Intent(context, SensingService::class.java).apply {
    putExtra(SensingService.EXTRA_UDP_ADDRESS, "192.168.1.100")
    putExtra(SensingService.EXTRA_UDP_PORT, 6666)
})
```

---

## Android Service との関係

Service は UI 層の責務なのでライブラリ本体（sensing / storage / network / pipeline）には含めない。
アプリ側で薄いラッパー Service を書いて Pipeline を内部で使う。

```kotlin
class MySensingService : Service() {
    private lateinit var pipeline: SensorPipeline

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        pipeline = SensorPipeline(SensorPipelineConfig(/* 設定 */))
        pipeline.start()
        return START_STICKY
    }

    override fun onDestroy() { pipeline.stop() }
    override fun onBind(intent: Intent?) = null
}
```

---

## 設計メモ

- `BaseSensorCollector` のリスナーはコンストラクタではなく `start(listener)` で渡す設計にした。
  Pipeline が listener を組み立ててから渡せるため、Collector 単体の再利用性が高まる。
- `SensorPipeline.stop()` では `sendExecutor.shutdown()` も呼ぶ。進行中の送信が完了してからスレッドが停止する。
