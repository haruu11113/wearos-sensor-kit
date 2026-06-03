package com.haruu11113.wearossensorkit.pipeline

import com.haruu11113.wearossensorkit.sensing.SensorData

interface SensorConsumer {
    fun onData(data: SensorData)
    fun onStop() {}
}
