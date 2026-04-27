# #007 SensorDataSerializer インターフェースの定義

## 概要
`SensorData` を文字列（JSON 等）に変換するための抽象インターフェースを定義する。
実装（JSON / CSV 等）を差し替えられるようにする。

## やること
- `storage/SensorDataSerializer.kt` を新規作成する

```kotlin
interface SensorDataSerializer {
    fun serialize(data: SensorData): String
}
```

## 完了条件
- `network` / `pipeline` パッケージには依存していない
- `sensing` パッケージの `SensorData` のみをインポートしている

## 依存
- #001 SensorData データクラスの定義
