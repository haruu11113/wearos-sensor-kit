package com.example.wearos.pipeline

import com.example.wearos.sensing.SensorData

interface SensorConsumer {
    fun onData(data: SensorData)
}
