package com.example.wearos.pipeline

import android.content.Context
import com.example.wearos.network.DataSender
import com.example.wearos.sensing.BaseSensorCollector
import com.example.wearos.storage.SensorDataStore
import com.example.wearos.storage.SQLiteStore

class SensorPipelineFactory(
    context: Context,
    val store: SensorDataStore = SQLiteStore(context),
    val serializer: SensorDataSerializer = JsonSerializer()
) {
    fun storeConsumer(): StoreConsumer = StoreConsumer(store, serializer)
    fun senderConsumer(sender: DataSender): SenderConsumer = SenderConsumer(sender, serializer)

    fun buildPipeline(
        collectors: List<BaseSensorCollector>,
        consumers: List<SensorConsumer>
    ): SensorPipeline = SensorPipeline(collectors, consumers)

    fun buildSyncJob(sender: DataSender): SyncJob = SyncJob(store, sender)
}
