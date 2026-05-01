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

### storage — ローカル保存

**責務**: `SensorData` を JSON 文字列としてローカルに保存する。取得方法も送信方法も知らない。

```
storage/src/main/java/com/example/wearos/storage/
├── SensorDataStore.kt   インターフェース: save(String):Long / readAll() / delete(List<Long>)
├── LocalFileStore.kt    実装: アプリ内ストレージへ JSONL 追記
└── SQLiteStore.kt       実装: SQLite による保存（store-and-forward 向け、デフォルト）
```

`SQLiteStore` は AUTOINCREMENT の ID を持つため、高頻度書き込み中でも安全な部分削除が可能。
`LocalFileStore` は行番号を ID として扱うシンプルな実装（書き捨て用途向け）。

---

### network — データ送信

**責務**: 文字列ペイロードを外部へ送る。センサーも保存も知らない。

```
network/src/main/java/com/example/wearos/network/
├── DataSender.kt      インターフェース: send(payload: String)
├── UdpSender.kt       実装: UDP 送信（DatagramSocket）
├── HttpSender.kt      実装: HTTP POST 送信
└── FirestoreSender.kt 実装: Cloud Firestore バッチ書き込み
```

`UdpSender` はコンストラクタで `address` と `port` を受け取る。IP のハードコードなし。
`DataSender` を実装するだけで任意の送信先を追加できる。

---

### pipeline — 配線層

**責務**: sensing / storage / network を組み合わせる。`SensorConsumer` インターフェースで処理を統一し、Store / Sender / ML 推論など任意の Consumer をリストで追加できる。

```
pipeline/src/main/java/com/example/wearos/pipeline/
├── SensorDataSerializer.kt   インターフェース: serialize(SensorData): String
├── JsonSerializer.kt         実装: JSON 文字列を返す
├── SensorConsumer.kt         インターフェース: onData(SensorData) / onStop()
├── StoreConsumer.kt          Consumer → SensorDataStore へ非同期保存
├── SenderConsumer.kt         Consumer → DataSender へ非同期送信
├── SensorPipeline.kt         start() / stop() でフロー全体を制御
├── SyncJob.kt                Store 蓄積データを Sender へ一括転送（store-and-forward）
└── SensorPipelineFactory.kt  Pipeline / SyncJob を組み立てるファクトリ
```

```kotlin
// SensorPipelineFactory が組み立てを担う
val factory = SensorPipelineFactory(context)  // デフォルト store は SQLiteStore

val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(context)),
    consumers  = listOf(factory.storeConsumer())   // 独自 Consumer も追加可
)
pipeline.start()

// store-and-forward: 任意タイミングで送信
factory.buildSyncJob(UdpSender("192.168.1.100", 6666)).execute()
```

`StoreConsumer` / `SenderConsumer` はそれぞれ `newSingleThreadExecutor` でバックグラウンド実行し、`onStop()` でシャットダウンを待機する。

---

### app — サンプルアプリ

`SensingService` が `SensorPipelineFactory` を使って Pipeline を生成・保持する薄いラッパー Service。

```kotlin
class MySensingService : Service() {
    private lateinit var pipeline: SensorPipeline

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val factory = SensorPipelineFactory(this)
        pipeline = factory.buildPipeline(
            collectors = listOf(AccelerometerCollector(this)),
            consumers  = listOf(factory.senderConsumer(UdpSender("192.168.1.100", 6666)))
        )
        pipeline.start()
        return START_STICKY
    }

    override fun onDestroy() { pipeline.stop() }
    override fun onBind(intent: Intent?) = null
}
```

---

## Android Service との関係

Service は UI 層の責務なのでライブラリ本体（sensing / storage / network / pipeline）には含めない。
アプリ側で薄いラッパー Service を書いて Pipeline を内部で使う。

---

## 設計メモ

- `BaseSensorCollector` のリスナーはコンストラクタではなく `start(listener)` で渡す設計にした。
  Pipeline が listener を組み立ててから渡せるため、Collector 単体の再利用性が高まる。
- `StoreConsumer` / `SenderConsumer` の `onStop()` は executor のシャットダウンを最大 5 秒待機する。未送信データの損失を防ぐため `pipeline.stop()` は必ず呼ぶこと。
- `SyncJob` は送信失敗時に即 return してリトライを次回に委ねる。送信成功分のみ削除するため冪等性が保たれる。
