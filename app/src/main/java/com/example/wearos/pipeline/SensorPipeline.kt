package com.example.wearos.pipeline

import com.example.wearos.sensing.SensorCollectorListener
import com.example.wearos.sensing.SensorData
import java.util.concurrent.Executors

class SensorPipeline(private val config: SensorPipelineConfig) {

    private val sendExecutor = Executors.newSingleThreadExecutor()

    fun start() {
        val listener = object : SensorCollectorListener {
            override fun onSensorData(data: SensorData) {
                val serialized = config.serializer.serialize(data)
                config.store?.save(serialized)
                sendExecutor.execute { config.sender?.send(serialized) }
            }
        }
        config.collectors.forEach { it.start(listener) }
    }

    fun stop() {
        config.collectors.forEach { it.stop() }
    }
}
