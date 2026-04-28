package com.example.wearos.storage

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import org.json.JSONObject

/**
 * SensorData を Cloud Firestore に保存する SensorDataStore 実装。
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
 * @param collection 保存先の Firestore コレクション名（デフォルト: "sensor_data"）
 * @param batchSize  この件数ごとに WriteBatch でまとめて書き込む（デフォルト: 20）。
 *                   高頻度センサー（SENSOR_DELAY_GAME）では 1 イベント = 1 書き込みだと
 *                   Firestore クォータを大量消費するため、まとめて書き込むことを推奨する。
 *                   [stop] を呼ぶと未送信のバッファも強制フラッシュされる。
 *
 * **注意:** [readAll] と [clear] は Firestore の非同期 API の性質上サポートしていない。
 */
class FirestoreStore(
    private val collection: String = "sensor_data",
    private val batchSize: Int = 20
) : SensorDataStore {

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

    override fun save(serialized: String) {
        try {
            val json = JSONObject(serialized)
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
            Log.e("FirestoreStore", "Failed to parse sensor data: ${e.message}", e)
        }
    }

    /** センシング停止時に呼ぶ。バッファに残った未送信データを強制フラッシュする。 */
    fun stop() {
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
            Log.e("FirestoreStore", "Failed to commit batch: ${e.message}", e)
        }
    }

    override fun readAll(): List<String> {
        throw UnsupportedOperationException(
            "FirestoreStore does not support synchronous readAll(). " +
            "Use the configured Firestore instance to read from collection \"$collection\" asynchronously."
        )
    }

    override fun clear() {
        throw UnsupportedOperationException(
            "FirestoreStore does not support clear(). " +
            "Delete documents directly via the Firebase Console or Admin SDK."
        )
    }
}
