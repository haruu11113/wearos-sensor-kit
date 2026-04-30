package com.example.wearos.pipeline

import android.util.Log
import com.example.wearos.network.DataSender
import com.example.wearos.storage.SensorDataStore

class SyncJob(
    private val store: SensorDataStore,
    private val sender: DataSender,
    private val chunkSize: Int = DEFAULT_CHUNK_SIZE
) {
    fun execute() {
        val items = store.readAll()
        if (items.isEmpty()) return

        items.chunked(chunkSize).forEach { chunk ->
            val ids = chunk.map { it.first }
            val payload = aggregate(chunk.map { it.second })
            try {
                sender.send(payload)
                store.delete(ids)
            } catch (e: Exception) {
                Log.e(TAG, "送信失敗。次回リトライ", e)
                return
            }
        }
    }

    private fun aggregate(items: List<String>): String =
        "[${items.joinToString(",")}]"

    companion object {
        private const val TAG = "SyncJob"
        private const val DEFAULT_CHUNK_SIZE = 100
    }
}
