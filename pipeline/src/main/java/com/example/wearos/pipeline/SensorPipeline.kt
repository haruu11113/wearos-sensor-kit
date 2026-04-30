package com.example.wearos.pipeline

import com.example.wearos.sensing.BaseSensorCollector
import com.example.wearos.sensing.SensorCollectorListener
import com.example.wearos.sensing.SensorData

class SensorPipeline(
    private val collectors: List<BaseSensorCollector>,
    private val consumers: List<SensorConsumer>
) {
    fun start() {
        val listener = object : SensorCollectorListener {
            override fun onSensorData(data: SensorData) {
                consumers.forEach { it.onData(data) }
            }
        }
        collectors.forEach { it.start(listener) }
    }

    fun stop() {
        collectors.forEach { it.stop() }
        consumers.filterIsInstance<SenderConsumer>().forEach { it.onStop() }
    }
}
