package com.haruu11113.wearossensorkit.storage

interface SensorDataStore {
    fun save(data: String): Long                      // 保存してIDを返す（変更）
    fun readAll(): List<Pair<Long, String>>            // ID付きで返す（変更）
    fun delete(ids: List<Long>)                       // 指定IDのみ削除（clear→delete に変更）
}
