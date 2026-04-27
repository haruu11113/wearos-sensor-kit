package com.example.wearos.storage

import android.content.Context
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class LocalFileStore(
    private val context: Context,
    private val fileName: String = "sensor_data.jsonl"
) : SensorDataStore {

    private val file: File
        get() = File(context.filesDir, fileName)

    override fun save(serialized: String) {
        BufferedWriter(FileWriter(file, true)).use { writer ->
            writer.write(serialized)
            writer.newLine()
        }
    }

    override fun readAll(): List<String> {
        if (!file.exists()) return emptyList()
        return file.bufferedReader(Charsets.UTF_8).useLines { lines ->
            lines.filter { it.isNotEmpty() }.toList()
        }
    }

    override fun clear() {
        if (file.exists()) {
            file.delete()
        }
    }
}
