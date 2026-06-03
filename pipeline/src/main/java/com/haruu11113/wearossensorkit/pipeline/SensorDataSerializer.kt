package com.haruu11113.wearossensorkit.pipeline

import com.haruu11113.wearossensorkit.sensing.SensorData

interface SensorDataSerializer {
    fun serialize(data: SensorData): String
}
