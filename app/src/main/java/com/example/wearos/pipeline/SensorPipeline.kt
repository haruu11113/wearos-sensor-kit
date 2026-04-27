package com.example.wearos.pipeline

import com.example.wearos.sensing.SensorCollectorListener
import com.example.wearos.sensing.SensorData

class SensorPipeline(private val config: SensorPipelineConfig) {

    fun start() {
        val listener = object : SensorCollectorListener {
            override fun onSensorData(data: SensorData) {
                val serialized = config.serializer.serialize(data)
                config.store?.save(serialized)
                config.sender?.let { sender ->
                    Thread { sender.send(serialized) }.start()
                }
            }
        }
        config.collectors.forEach { it.start(listener) }
    }

    fun stop() {
        config.collectors.forEach { it.stop() }
    }
}
