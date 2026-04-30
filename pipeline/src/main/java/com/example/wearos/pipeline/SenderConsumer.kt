package com.example.wearos.pipeline

import com.example.wearos.network.DataSender
import com.example.wearos.sensing.SensorData

class SenderConsumer(
    private val sender: DataSender,
    private val serializer: SensorDataSerializer = JsonSerializer()
) : SensorConsumer {

    override fun onData(data: SensorData) {
        val serialized = serializer.serialize(data)
        sender.send(serialized)
    }

    override fun onStop() {
        sender.onStop()
    }
}
