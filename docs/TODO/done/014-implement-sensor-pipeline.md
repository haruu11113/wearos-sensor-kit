# #014 SensorPipeline の実装

## 概要
`SensorPipelineConfig` を受け取り、sensing → storage → network の全フローを組み立て・制御するクラスを実装する。
これがライブラリの中心となるエントリーポイント。

## やること
- `pipeline/SensorPipeline.kt` を新規作成する
- `start()`: 各 Collector に `SensorCollectorListener` を配線して `start()` を呼ぶ
- `stop()`: 各 Collector の `stop()` を呼ぶ
- リスナー内の処理:
  1. `serializer.serialize(data)` で文字列化
  2. `store?.save(serialized)` でローカル保存（null なら省略）
  3. `sender?.send(serialized)` でネットワーク送信（null なら省略）
  4. 送信はバックグラウンドスレッド（`Thread { }.start()` または Coroutine）で行う

```kotlin
class SensorPipeline(private val config: SensorPipelineConfig) {
    fun start() { /* collectors を起動し listener を配線 */ }
    fun stop()  { /* collectors を全停止 */ }
}
```

## 完了条件
- `start()` → `stop()` のライフサイクルが正しく動く
- `store` / `sender` が `null` でも例外が出ない
- ネットワーク送信がメインスレッドをブロックしない

## 依存
- #013 SensorPipelineConfig の定義
- #004〜#006 各 Collector の実装
- #008 JsonSerializer の実装
- #010 LocalFileStore の実装
- #012 UdpSender のリファクタリング
