# 039 SensorPipelineFactory の実装

## 概要

依存インスタンスの生成・保持・組み立てを担う Factory クラスを実装する。
`store` と `serializer` を共有インスタンスとして保持し、
Consumer / Pipeline / SyncJob を生成して返す。

## 前提

- 031〜038 すべて完了済みであること

## 対象ファイル

新規作成: `pipeline/src/main/java/com/example/wearos/pipeline/SensorPipelineFactory.kt`

## 実装内容

```kotlin
package com.example.wearos.pipeline

import android.content.Context
import com.example.wearos.network.DataSender
import com.example.wearos.sensing.BaseSensorCollector
import com.example.wearos.storage.SensorDataStore
import com.example.wearos.storage.SQLiteStore

class SensorPipelineFactory(
    context: Context,
    val store:      SensorDataStore      = SQLiteStore(context),
    val serializer: SensorDataSerializer = JsonSerializer()
) {
    // Consumer の生成（store / serializer を注入済み）
    fun storeConsumer():               StoreConsumer  = StoreConsumer(store, serializer)
    fun senderConsumer(sender: DataSender): SenderConsumer = SenderConsumer(sender, serializer)

    // Pipeline の生成
    fun buildPipeline(
        collectors: List<BaseSensorCollector>,
        consumers:  List<SensorConsumer>
    ): SensorPipeline = SensorPipeline(collectors, consumers)

    // SyncJob の生成（store を注入済み）
    fun buildSyncJob(sender: DataSender): SyncJob = SyncJob(store, sender)
}
```

## 利用例

```kotlin
val factory = SensorPipelineFactory(context)

// UDP リアルタイム
val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(context)),
    consumers  = listOf(factory.senderConsumer(UdpSender("192.168.1.100", 6666)))
)

// Store 蓄積 → あとで Firestore へ送信
val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(context)),
    consumers  = listOf(factory.storeConsumer())
)
val syncJob = factory.buildSyncJob(FirestoreSender())
syncJob.execute()

// Store 蓄積 + オンデバイス推論
val pipeline = factory.buildPipeline(
    collectors = listOf(AccelerometerCollector(context), GyroscopeCollector(context)),
    consumers  = listOf(
        factory.storeConsumer(),
        ActivityRecognizer(model)    // 利用側が実装
    )
)
```

## 完了条件

- `SensorPipelineFactory.kt` が作成されている
- ビルドが通ること
