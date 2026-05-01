# 040 USAGE.md を新アーキテクチャに合わせて更新

## 概要

039 完了後、`docs/USAGE.md` のコード例を新アーキテクチャ（SensorPipelineFactory ベース）に書き直す。

**このタスクは 039 完了後に実施すること。**

## 対象ファイル

変更: `docs/USAGE.md`

## 変更内容

### 削除・書き換えが必要な箇所

- `SensorPipelineConfig` を使った古いコード例をすべて削除
- `SensorPipelineFactory` を使った新しいコード例に差し替え

### 新しいコード例の構成

#### 最小構成（加速度 + UDP 送信）

```kotlin
val factory = SensorPipelineFactory(context)

val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this)),
    consumers  = listOf(factory.senderConsumer(UdpSender("192.168.1.100", 6666)))
)
pipeline.start()
```

#### Store に蓄積 → あとで Firestore に送信

```kotlin
val factory = SensorPipelineFactory(context)

// 収集：常時 Store に保存
val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this)),
    consumers  = listOf(factory.storeConsumer())
)
pipeline.start()

// 送信：任意のタイミングで呼ぶ
val syncJob = factory.buildSyncJob(FirestoreSender())
syncJob.execute()
```

#### Store 蓄積 + オンデバイス推論

```kotlin
val factory = SensorPipelineFactory(context)

val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(this), GyroscopeCollector(this)),
    consumers  = listOf(
        factory.storeConsumer(),
        ActivityRecognizer(model)   // 利用側が実装
    )
)
pipeline.start()
```

#### 独自 Consumer の実装例

```kotlin
class MyConsumer : SensorConsumer {
    override fun onData(data: SensorData) {
        // 好きな処理
    }
}
```

## 完了条件

- `SensorPipelineConfig` を使ったコード例がなくなっている
- 3パターンのコード例が記載されている
- Collector 一覧テーブルはそのまま維持する
