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

    override fun save(data: String): Long {
        BufferedWriter(FileWriter(file, true)).use { it.write(data); it.newLine() }
        return file.readLines().size.toLong()
    }

    override fun readAll(): List<Pair<Long, String>> {
        if (!file.exists()) return emptyList()
        return file.readLines()
            .mapIndexedNotNull { index, line ->
                if (line.isNotEmpty()) Pair(index + 1L, line) else null
            }
    }

    override fun delete(ids: List<Long>) {
        if (!file.exists()) return
        val remaining = file.readLines()
            .mapIndexedNotNull { index, line ->
                if ((index + 1L) !in ids) line else null
            }
        file.writeText(remaining.joinToString("\n") + if (remaining.isNotEmpty()) "\n" else "")
    }
}
