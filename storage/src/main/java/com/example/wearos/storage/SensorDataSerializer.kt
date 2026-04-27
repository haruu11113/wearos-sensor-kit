package com.example.wearos.storage

import com.example.wearos.sensing.SensorData

interface SensorDataSerializer {
    fun serialize(data: SensorData): String
}
