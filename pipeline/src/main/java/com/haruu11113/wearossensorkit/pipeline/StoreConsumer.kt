package com.haruu11113.wearossensorkit.pipeline

import com.haruu11113.wearossensorkit.sensing.SensorData
import com.haruu11113.wearossensorkit.storage.SensorDataStore
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StoreConsumer(
    private val store: SensorDataStore,
    private val serializer: SensorDataSerializer = JsonSerializer()
) : SensorConsumer {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onData(data: SensorData) {
        val serialized = serializer.serialize(data)
        executor.execute { store.save(serialized) }
    }

    override fun onStop() {
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }
}
