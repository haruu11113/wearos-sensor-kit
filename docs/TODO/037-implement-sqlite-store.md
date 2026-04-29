# 037 SQLiteStore の実装

## 概要

`SensorDataStore` の SQLite 実装を追加する。
store-and-forward パターン（蓄積→後で送信→送信済み削除）に適した実装。
ROWID を ID として使うことで、高頻度書き込み中でも安全な部分削除が可能。

## 前提

- 035 完了済みであること（`SensorDataStore` インターフェースが更新されている）

## 対象ファイル

新規作成: `storage/src/main/java/com/example/wearos/storage/SQLiteStore.kt`

## 実装内容

```kotlin
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
```

## LocalFileStore との比較

| | LocalFileStore | SQLiteStore |
|--|---|---|
| ID 管理 | 行番号（脆弱） | AUTOINCREMENT（堅牢） |
| 部分削除 | 全件書き直し | DELETE WHERE id IN (...) |
| 並行書き込み | ロックが必要 | SQLite がトランザクション管理 |
| 用途 | シンプルな書き捨て | store-and-forward |

## 完了条件

- `SQLiteStore.kt` が作成されている
- `SensorDataStore` を実装している
- ビルドが通ること
