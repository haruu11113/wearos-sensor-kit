package com.example.wearos.storage

import com.example.wearos.sensing.SensorData
import org.json.JSONArray
import org.json.JSONObject

class JsonSerializer : SensorDataSerializer {
    override fun serialize(data: SensorData): String {
        val valuesArray = JSONArray()
        data.values.forEach { valuesArray.put(it.toDouble()) }
        return JSONObject()
            .put("type", data.type)
            .put("values", valuesArray)
            .put("timestamp_ns", data.timestampNs)
            .toString()
    }
}
