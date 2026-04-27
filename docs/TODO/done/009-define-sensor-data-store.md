# #009 SensorDataStore インターフェースの定義

## 概要
シリアライズ済み文字列を永続化するためのストレージ抽象インターフェースを定義する。
ファイル・DB・インメモリ等の実装を差し替えられるようにする。

## やること
- `storage/SensorDataStore.kt` を新規作成する

```kotlin
interface SensorDataStore {
    fun save(serialized: String)
    fun readAll(): List<String>
    fun clear()
}
```

## 完了条件
- `sensing` / `network` / `pipeline` パッケージに依存していない
- 純粋なインターフェースのみで Android フレームワークの import がない

## 依存
なし（型を持たない純粋インターフェース）
