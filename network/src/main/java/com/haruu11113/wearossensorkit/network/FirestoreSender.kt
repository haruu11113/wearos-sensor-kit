package com.haruu11113.wearossensorkit.network

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import org.json.JSONObject

/**
 * センサーデータを Cloud Firestore に送信する DataSender 実装。
 *
 * **前提条件（利用側アプリ）:**
 * - `google-services.json` をアプリモジュールに配置すること
 * - アプリの `build.gradle.kts` に `com.google.gms.google-services` プラグインを適用すること
 *
 * **Firestore のドキュメント構造:**
 * ```
 * {collection}/
 *   {auto-id}/
 *     type:             "accelerometer"
 *     values:           [0.12, -9.80, 0.05]
 *     timestamp_ns:     123456789
 *     server_timestamp: <Firestore サーバータイムスタンプ>
 * ```
 *
 * @param collection 送信先の Firestore コレクション名（デフォルト: "sensor_data"）
 * @param batchSize  この件数ごとに WriteBatch でまとめて書き込む（デフォルト: 20）。
 *                   高頻度センサー（SENSOR_DELAY_GAME）では 1 イベント = 1 書き込みだと
 *                   Firestore クォータを大量消費するため、まとめて書き込むことを推奨する。
 */
class FirestoreSender(
    private val collection: String = "sensor_data",
    private val batchSize: Int = 20
) : DataSender {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /** テスト用: FirebaseFirestore を差し替えられる internal コンストラクタ */
    internal constructor(
        collection: String,
        batchSize: Int,
        db: FirebaseFirestore
    ) : this(collection, batchSize) {
        _db = db
    }

    private var _db: FirebaseFirestore? = null
    private val firestore get() = _db ?: db

    private val buffer = mutableListOf<Map<String, Any>>()

    override fun send(payload: String) {
        try {
            val json = JSONObject(payload)
            val valuesArray = json.getJSONArray("values")
            val values = (0 until valuesArray.length()).map { valuesArray.getDouble(it) }

            val document = mapOf(
                "type"             to json.getString("type"),
                "values"           to values,
                "timestamp_ns"     to json.getLong("timestamp_ns"),
                "server_timestamp" to FieldValue.serverTimestamp()
            )

            synchronized(buffer) {
                buffer.add(document)
                if (buffer.size >= batchSize) flush()
            }
        } catch (e: Exception) {
            Log.e("FirestoreSender", "Failed to parse sensor data: ${e.message}", e)
        }
    }

    override fun onStop() {
        synchronized(buffer) { flush() }
    }

    private fun flush() {
        if (buffer.isEmpty()) return
        val batch: WriteBatch = firestore.batch()
        buffer.forEach { doc ->
            batch.set(firestore.collection(collection).document(), doc)
        }
        buffer.clear()
        batch.commit().addOnFailureListener { e ->
            Log.e("FirestoreSender", "Failed to commit batch: ${e.message}", e)
        }
    }
}
