# #003 BaseSensorCollector 抽象クラスの実装

## 概要
各センサー Collector の共通処理（SensorManager への登録・解除、イベントの受け取りと `SensorData` への変換）を抽象基底クラスにまとめる。
Android の `Service` には依存させない。`Context` だけを受け取る。

## やること
- `sensing/BaseSensorCollector.kt` を新規作成する
- `SensorEventListener` を実装する
- コンストラクタで `Context` と `SensorCollectorListener` を受け取る
- `start()` で SensorManager にリスナー登録、`stop()` で解除する
- `onSensorChanged()` → `formatData(event)` → `listener.onSensorData()` の流れを定義する
- サブクラスが実装すべき抽象メソッドを定義する:
  - `val sensorType: Int` — `Sensor.TYPE_XXX`
  - `val samplingRate: Int` — `SENSOR_DELAY_XXX`
  - `fun formatData(event: SensorEvent): SensorData`

```kotlin
abstract class BaseSensorCollector(
    context: Context,
    private val listener: SensorCollectorListener
) : SensorEventListener {
    abstract val sensorType: Int
    abstract val samplingRate: Int
    abstract fun formatData(event: SensorEvent): SensorData

    fun start() { /* SensorManager に登録 */ }
    fun stop()  { /* SensorManager から解除 */ }
}
```

## 完了条件
- `Service` を import していない
- `start()` / `stop()` を呼べばリスナー登録・解除できる
- `onSensorChanged()` が `listener.onSensorData()` を呼ぶ

## 依存
- #001 SensorData データクラスの定義
- #002 SensorCollectorListener インターフェースの定義
