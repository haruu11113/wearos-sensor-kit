# #012 UdpSender を DataSender インターフェースに合わせてリファクタリング

## 概要
既存の `UdpSender` が実装するインターフェースを `BaseSender` から `DataSender` に変更し、
メソッド名 `sendMessage()` → `send()` に統一する。
ハードコードされた IP アドレスをコンストラクタ引数のみで指定できるようにする。

## やること
- `network/UdpSender.kt` を修正する
  - `implements BaseSender` → `implements DataSender`
  - `fun sendMessage(message: String)` → `fun send(payload: String)`
  - クラス内にデフォルト IP / ポートのハードコードがあれば削除する
- `BaseSender.kt` を削除する

## 変更後のシグネチャ

```kotlin
class UdpSender(
    private val address: String,
    private val port: Int
) : DataSender {
    override fun send(payload: String) { /* UDP 送信 */ }
}
```

## 完了条件
- `UdpSender` が `DataSender` を実装している
- `BaseSender.kt` が存在しない
- IP アドレス・ポートがソースコード内にハードコードされていない

## 依存
- #011 DataSender インターフェースの定義
