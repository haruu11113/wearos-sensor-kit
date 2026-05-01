# 035 SensorDataStore インターフェースの更新

## 概要

`SensorDataStore` の `save()` に ID を返す戻り値を追加し、
`clear()` を `delete(ids)` に変更する。
SyncJob が「送信済みのデータだけを安全に削除」できるようにするための変更。

**このタスクを 031 と並行して早期に完了させること。032・036・037・038 が依存する。**

## 対象ファイル

変更: `storage/src/main/java/com/example/wearos/storage/SensorDataStore.kt`

## 変更後のインターフェース

```kotlin
package com.example.wearos.storage

interface SensorDataStore {
    fun save(data: String): Long                      // 保存してIDを返す（変更）
    fun readAll(): List<Pair<Long, String>>            // ID付きで返す（変更）
    fun delete(ids: List<Long>)                       // 指定IDのみ削除（clear→delete に変更）
}
```

## clear() を delete(ids) に変える理由

```
store.readAll() で ids=[1,2,3] を取得
    ↓ 送信中...（この間にも新データが ids=[4,5] で書き込まれる）
sender.send() 成功
    ↓
store.clear() → ids=[1,2,3,4,5] が全部消える ← 4,5 は未送信なのに消えてしまう！

store.delete([1,2,3]) → 送信済みの分だけ消える ✅
```

## 完了条件

- `SensorDataStore` インターフェースが上記の通り更新されている
- ビルドエラーが出る場合は後続タスク（036）で `LocalFileStore` を修正するため一時的に許容する
