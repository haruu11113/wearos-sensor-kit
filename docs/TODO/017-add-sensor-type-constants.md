# 017 SensorData に新センサの TYPE 定数を追加

## 概要

`SensorData.kt` の `companion object` に、新たに追加するすべてのセンサの型文字列定数を追加する。
**このタスクを最初に完了させること。018〜029 はこの定数に依存する。**

## 対象ファイル

`sensing/src/main/java/com/example/wearos/sensing/SensorData.kt`

## 変更内容

`companion object` に以下の定数を追記する:

```kotlin
companion object {
    // 既存
    const val TYPE_ACCELEROMETER = "accelerometer"
    const val TYPE_HEART_RATE    = "heart_rate"
    const val TYPE_LIGHT         = "light"

    // 追加
    const val TYPE_GYROSCOPE             = "gyroscope"
    const val TYPE_MAGNETIC_FIELD        = "magnetic_field"
    const val TYPE_ROTATION_VECTOR       = "rotation_vector"
    const val TYPE_STEP_COUNTER          = "step_counter"
    const val TYPE_STEP_DETECTOR         = "step_detector"
    const val TYPE_GRAVITY               = "gravity"
    const val TYPE_LINEAR_ACCELERATION   = "linear_acceleration"
    const val TYPE_PRESSURE              = "pressure"
    const val TYPE_HEART_BEAT            = "heart_beat"
    const val TYPE_OXYGEN_SATURATION     = "oxygen_saturation"
    const val TYPE_SKIN_TEMPERATURE      = "skin_temperature"
    const val TYPE_OFF_BODY_DETECT       = "off_body_detect"
}
```

## 完了条件

- 上記 12 定数が `SensorData.kt` に追加されている
- ビルドが通ること
