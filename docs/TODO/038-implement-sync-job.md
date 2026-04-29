# 038 SyncJob の実装

## 概要

Store に蓄積されたデータを読み出し、集約して送信し、送信済みを削除するクラスを実装する。
Pipeline とは独立して動き、いつ呼ぶかは利用側が決める。

## 前提

- 035 完了済みであること（`SensorDataStore` インターフェースが更新されている）

## 対象ファイル

新規作成: `pipeline/src/main/java/com/example/wearos/pipeline/SyncJob.kt`

## 実装内容

```kotlin
package com.example.wearos.pipeline

import android.util.Log
import com.example.wearos.network.DataSender
import com.example.wearos.storage.SensorDataStore

class SyncJob(
    private val store:      SensorDataStore,
    private val sender:     DataSender,
    private val chunkSize:  Int = DEFAULT_CHUNK_SIZE
) {
    fun execute() {
        val items = store.readAll()
        if (items.isEmpty()) return

        items.chunked(chunkSize).forEach { chunk ->
            val ids     = chunk.map { it.first }
            val payload = aggregate(chunk.map { it.second })
            try {
                sender.send(payload)
                store.delete(ids)           // 送信成功した分だけ削除
            } catch (e: Exception) {
                Log.e(TAG, "送信失敗。次回リトライ", e)
                return                      // 失敗したら中断（削除しない）
            }
        }
    }

    // 複数件の JSON を配列にまとめる
    private fun aggregate(items: List<String>): String =
        "[${items.joinToString(",")}]"

    companion object {
        private const val TAG = "SyncJob"
        private const val DEFAULT_CHUNK_SIZE = 100
    }
}
```

## 動作フロー

```
store.readAll() → ids=[1..N] を取得
chunkSize 件ずつループ:
    aggregate() → JSON 配列にまとめる
    sender.send() 成功 → store.delete(ids) 送信済み分のみ削除
    sender.send() 失敗 → 削除しない・中断（次回 execute() で再試行）
```

## 注意事項

- `execute()` はバックグラウンドスレッドで呼ぶこと（ネットワーク処理のため）
- いつ呼ぶか（ネット接続時・WorkManager・手動など）は利用側が決める
- `SyncJob` は Pipeline を知らない・Pipeline は SyncJob を知らない

## 完了条件

- `SyncJob.kt` が作成されている
- ビルドが通ること
