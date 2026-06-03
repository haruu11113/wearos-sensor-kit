package com.haruu11113.wearossensorkit.pipeline

import com.haruu11113.wearossensorkit.sensing.BaseSensorCollector
import com.haruu11113.wearossensorkit.sensing.SensorCollectorListener
import com.haruu11113.wearossensorkit.sensing.SensorData

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
        consumers.forEach { it.onStop() }
    }
}
