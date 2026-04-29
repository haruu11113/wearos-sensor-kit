# 036 LocalFileStore の更新

## 概要

035 の `SensorDataStore` インターフェース変更に合わせて `LocalFileStore` を更新する。
`save()` が ID を返す形に変更し、`clear()` を `delete(ids)` に変更する。

## 前提

- 035 完了済みであること（`SensorDataStore` インターフェースが更新されている）

## 対象ファイル

変更: `storage/src/main/java/com/example/wearos/storage/LocalFileStore.kt`

## 実装方針

JSON Lines ファイルは行番号を ID として扱う。
ただし `delete(ids)` で中間行を削除する場合、全行読み直して書き直す必要がある。

```kotlin
class LocalFileStore(
    private val context: Context,
    private val fileName: String = "sensor_data.jsonl"
) : SensorDataStore {

    private val file: File
        get() = File(context.filesDir, fileName)

    // 追記して、追記後の行番号（1始まり）を ID として返す
    override fun save(data: String): Long {
        BufferedWriter(FileWriter(file, true)).use { it.write(data); it.newLine() }
        return file.readLines().size.toLong()
    }

    // 行番号（1始まり）を ID として Pair<Long, String> で返す
    override fun readAll(): List<Pair<Long, String>> {
        if (!file.exists()) return emptyList()
        return file.readLines()
            .mapIndexedNotNull { index, line ->
                if (line.isNotEmpty()) Pair(index + 1L, line) else null
            }
    }

    // 指定 ID（行番号）以外を残して書き直す
    override fun delete(ids: List<Long>) {
        if (!file.exists()) return
        val remaining = file.readLines()
            .mapIndexedNotNull { index, line ->
                if ((index + 1L) !in ids) line else null
            }
        file.writeText(remaining.joinToString("\n") + if (remaining.isNotEmpty()) "\n" else "")
    }
}
```

## 注意事項

- LocalFileStore の ID 管理は行番号ベースのため、大量データや高頻度削除には向かない
- 本格的な store-and-forward 用途には SQLiteStore（037）を推奨
- シンプルな用途（書き捨て・全件送信）では引き続き LocalFileStore で十分

## 完了条件

- `LocalFileStore` が更新されたインターフェースを実装している
- ビルドが通ること
