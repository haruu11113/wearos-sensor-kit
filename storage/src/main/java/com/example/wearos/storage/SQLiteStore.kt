package com.example.wearos.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteStore(context: Context) : SensorDataStore {

    private val helper = object : SQLiteOpenHelper(context, "sensor_data.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE sensor_data (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT NOT NULL)"
            )
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS sensor_data")
            onCreate(db)
        }
    }

    override fun save(data: String): Long {
        val values = ContentValues().apply { put("data", data) }
        return helper.writableDatabase.insert("sensor_data", null, values)
    }

    override fun readAll(): List<Pair<Long, String>> {
        val result = mutableListOf<Pair<Long, String>>()
        helper.readableDatabase
            .rawQuery("SELECT id, data FROM sensor_data ORDER BY id ASC", null)
            .use { cursor ->
                while (cursor.moveToNext()) {
                    result.add(Pair(cursor.getLong(0), cursor.getString(1)))
                }
            }
        return result
    }

    override fun delete(ids: List<Long>) {
        if (ids.isEmpty()) return
        val placeholders = ids.joinToString(",") { "?" }
        helper.writableDatabase.execSQL(
            "DELETE FROM sensor_data WHERE id IN ($placeholders)",
            ids.map { it.toString() }.toTypedArray()
        )
    }
}
