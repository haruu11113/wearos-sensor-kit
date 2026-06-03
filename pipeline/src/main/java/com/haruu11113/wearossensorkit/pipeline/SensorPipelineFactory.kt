package com.haruu11113.wearossensorkit.pipeline

import android.content.Context
import com.haruu11113.wearossensorkit.network.DataSender
import com.haruu11113.wearossensorkit.sensing.BaseSensorCollector
import com.haruu11113.wearossensorkit.storage.SensorDataStore
import com.haruu11113.wearossensorkit.storage.SQLiteStore

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
