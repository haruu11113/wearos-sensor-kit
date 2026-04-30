package com.example.wearos.pipeline

import com.example.wearos.sensing.SensorData
import com.example.wearos.storage.SensorDataStore

class StoreConsumer(
    private val store: SensorDataStore,
    private val serializer: SensorDataSerializer = JsonSerializer()
) : SensorConsumer {

    override fun onData(data: SensorData) {
        val serialized = serializer.serialize(data)
        store.save(serialized)
    }
}
