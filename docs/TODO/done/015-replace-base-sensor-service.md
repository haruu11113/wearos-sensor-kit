# #015 既存の BaseSensorService を SensorPipeline ベースに置き換え

## 概要
現在の `BaseSensorService` / `AccelerometerSensorService` / `HeartRateSensorService` / `LightSensorService` を
`SensorPipeline` を内部で使う薄いラッパー Service に置き換える。
`MainActivity` の Service 起動コードも合わせて修正する。

## やること

### Service の整理
- `BaseSensorService.kt` の中身を削除し、`SensorPipeline` を保持するだけの薄いクラスに書き換える
- `AccelerometerSensorService` / `HeartRateSensorService` / `LightSensorService` を削除する
- 代わりに単一の `SensingService.kt` を作成し、3 つの Collector をまとめて Pipeline に渡す

```kotlin
class SensingService : Service() {
    private lateinit var pipeline: SensorPipeline

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val config = SensorPipelineConfig(
            collectors = listOf(
                AccelerometerCollector(this),
                HeartRateCollector(this),
                LightCollector(this)
            ),
            sender = UdpSender(
                address = intent?.getStringExtra("udp_address") ?: "192.168.50.78",
                port    = intent?.getIntExtra("udp_port", 6666) ?: 6666
            )
        )
        pipeline = SensorPipeline(config)
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

### MainActivity の修正
- `AccelerometerSensorService` / `HeartRateSensorService` / `LightSensorService` の import を削除
- Start / Stop で `SensingService` を 1 つ起動・停止するように変更
- UDP 送信先を Intent の extras で渡す

### AndroidManifest の修正
- 旧 3 Service の宣言を削除し `SensingService` を追加する

## 完了条件
- `BaseSensorService.kt`, `AccelerometerSensorService.kt`, `HeartRateSensorService.kt`, `LightSensorService.kt` が削除または空化されている
- `SensingService.kt` 1 つで 3 センサーが動く
- Start / Stop ボタンで正常にセンシングが開始・停止する

## 依存
- #014 SensorPipeline の実装
