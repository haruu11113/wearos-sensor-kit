# #011 DataSender インターフェースの定義

## 概要
文字列ペイロードを外部へ送信するための抽象インターフェースを定義する。
現在の `BaseSender` を廃止してこちらに統一する。

## やること
- `network/DataSender.kt` を新規作成する

```kotlin
interface DataSender {
    fun send(payload: String)
}
```

- 既存の `network/BaseSender.kt` を削除する（`UdpSender` が `DataSender` を実装するよう変更後に削除）

## 完了条件
- `DataSender.kt` が `com.example.wearos.network` パッケージに存在する
- `BaseSender.kt` が削除されている（#012 完了後）
- `sensing` / `storage` / `pipeline` パッケージに依存していない

## 依存
なし（#012 と合わせて実施）
