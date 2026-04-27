# #002 SensorCollectorListener インターフェースの定義

## 概要
センサーデータをコールバックで受け取るためのインターフェースを定義する。
sensing 層の外部（pipeline 等）がデータを受け取る唯一の入口になる。

## やること
- `sensing/SensorCollectorListener.kt` を新規作成する

```kotlin
interface SensorCollectorListener {
    fun onSensorData(data: SensorData)
}
```

## 完了条件
- `SensorCollectorListener.kt` が `com.example.wearos.sensing` パッケージに存在する
- `SensorData` のみをインポートしており、network / storage には依存していない

## 依存
- #001 SensorData データクラスの定義
