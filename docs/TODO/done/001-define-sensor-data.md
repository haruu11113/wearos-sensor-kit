# #001 SensorData データクラスの定義

## 概要
センサーの計測値を表す共通の型 `SensorData` を定義する。
この型が sensing / storage / pipeline の共通言語になる。

## やること
- `sensing/SensorData.kt` を新規作成する
- フィールド: `type: String`, `values: FloatArray`, `timestampNs: Long`
- センサー種別は文字列定数として `companion object` で定義する (`"accelerometer"`, `"heart_rate"`, `"light"`)

```kotlin
data class SensorData(
    val type: String,
    val values: FloatArray,
    val timestampNs: Long
) {
    companion object {
        const val TYPE_ACCELEROMETER = "accelerometer"
        const val TYPE_HEART_RATE    = "heart_rate"
        const val TYPE_LIGHT         = "light"
    }
}
```

## 完了条件
- `SensorData.kt` が `com.example.wearos.sensing` パッケージに存在する
- `FloatArray` を持つため `equals` / `hashCode` / `copy` が正しく動くことを確認（必要なら手動オーバーライド）

## 依存
なし（最初に作るもの）
