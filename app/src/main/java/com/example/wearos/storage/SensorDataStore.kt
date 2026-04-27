package com.example.wearos.storage

interface SensorDataStore {
    fun save(serialized: String)
    fun readAll(): List<String>
    fun clear()
}
