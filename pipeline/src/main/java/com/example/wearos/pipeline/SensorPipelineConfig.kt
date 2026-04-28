package com.example.wearos.pipeline

import com.example.wearos.network.DataSender
import com.example.wearos.sensing.BaseSensorCollector
import com.example.wearos.storage.SensorDataStore

data class SensorPipelineConfig(
    val collectors: List<BaseSensorCollector>,
    val serializer: SensorDataSerializer = JsonSerializer(),
    val store: SensorDataStore? = null,
    val sender: DataSender? = null
)
