package com.example.wearos.pipeline

import com.example.wearos.sensing.SensorData

interface SensorDataSerializer {
    fun serialize(data: SensorData): String
}
