package com.example.wearos.storage

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
 *     type:           "accelerometer"
 *     values:         [0.12, -9.80, 0.05]
 *     timestamp_ns:   123456789
 *     server_timestamp: <Firestore サーバータイムスタンプ>
 * ```
 *
 * **注意:** [readAll] と [clear] は Firestore の非同期 API の性質上サポートしていない。
 */
class FirestoreStore(
    private val collection: String = "sensor_data",
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : SensorDataStore {

    override fun save(serialized: String) {
        try {
            val json = JSONObject(serialized)
            val valuesArray = json.getJSONArray("values")
            val values = (0 until valuesArray.length()).map { valuesArray.getDouble(it) }

            val document = hashMapOf(
                "type"             to json.getString("type"),
                "values"           to values,
                "timestamp_ns"     to json.getLong("timestamp_ns"),
                "server_timestamp" to FieldValue.serverTimestamp()
            )

            db.collection(collection)
                .add(document)
                .addOnFailureListener { e ->
                    Log.e("FirestoreStore", "Failed to save document: $e")
                }
        } catch (e: Exception) {
            Log.e("FirestoreStore", "Failed to parse or save: $e")
        }
    }

    override fun readAll(): List<String> {
        throw UnsupportedOperationException(
            "FirestoreStore does not support synchronous readAll(). " +
            "Use FirebaseFirestore.getInstance().collection(\"$collection\").get() directly."
        )
    }

    override fun clear() {
        throw UnsupportedOperationException(
            "FirestoreStore does not support clear(). " +
            "Delete documents directly via the Firebase Console or Admin SDK."
        )
    }
}
