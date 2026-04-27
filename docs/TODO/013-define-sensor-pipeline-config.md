# #013 SensorPipelineConfig の定義

## 概要
`SensorPipeline` に渡す設定をまとめた data class を定義する。
どの Collector を使い、どのシリアライザ・ストア・センダーを使うかをここで指定する。

## やること
- `pipeline/SensorPipelineConfig.kt` を新規作成する

```kotlin
data class SensorPipelineConfig(
    val collectors: List<BaseSensorCollector>,
    val serializer: SensorDataSerializer = JsonSerializer(),
    val store: SensorDataStore? = null,   // null なら保存スキップ
    val sender: DataSender? = null        // null なら送信スキップ
)
```

- `store` と `sender` はどちらも `null` 可（保存だけ・送信だけ・両方・どちらもなしを選べる）

## 完了条件
- `SensorPipelineConfig.kt` が `com.example.wearos.pipeline` パッケージに存在する
- デフォルト引数で `JsonSerializer()` が使われる
- 全フィールドがインターフェース型（実装クラスを直接持たない）

## 依存
- #003 BaseSensorCollector 抽象クラスの実装
- #007 SensorDataSerializer インターフェースの定義
- #008 JsonSerializer の実装（デフォルト値として参照）
- #009 SensorDataStore インターフェースの定義
- #011 DataSender インターフェースの定義
