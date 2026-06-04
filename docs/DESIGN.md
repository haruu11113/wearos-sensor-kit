# 設計ドキュメント

ライブラリのアーキテクチャと、なぜそういう構造にしたかをまとめます。
**コード例・使い方は [USAGE.md](USAGE.md) 参照。**

---

## 設計原則

**「センシング・保存・送信を完全に分離し、組み合わせ自由にする」**

- 上位レイヤーは下位の実装クラスを直接 import しない
- 差し替えはすべてインターフェース経由
- Pipeline は配線役だけ。整形・保存・送信ロジックは持たない

---

## モジュール依存関係

```
sensing   ───▶ (なし)        センサー値の取得だけ
storage   ───▶ sensing       SensorData の型だけ参照
network   ───▶ (なし)        文字列を送るだけ
pipeline  ───▶ 全モジュール   唯一の配線場所
app       ───▶ pipeline のみ  モジュール内部を直接触らない
```

`pipeline` モジュールが他の3つを `api` で再エクスポートしているため、
利用側は `pipeline` への依存だけで全機能が使えます。

---

## モジュール詳細

### sensing — センサーデータ取得

**責務**: センサーから値を読み取り、型付きの `SensorData` として通知する。ネットワークや保存は知らない。

主要クラス:

| クラス | 役割 |
|--------|------|
| `SensorData` | データクラス `(type, values, timestampNs)` |
| `SensorCollectorListener` | コールバックインターフェース |
| `BaseSensorCollector` | 抽象基底。SensorManager 登録/解除 |
| `*Collector`（15種類） | 各センサーの実装 |

`BaseSensorCollector.start(listener)` で登録、`stop()` で解除。
サブクラスは `sensorType`・`samplingRate`・`formatData()` の 3 つを実装するだけ。

```kotlin
data class SensorData(
    val type: String,        // "accelerometer" 等
    val values: FloatArray,
    val timestampNs: Long
)
```

---

### storage — ローカル保存

**責務**: シリアライズ済み文字列をローカルに保存する。送信方法は知らない。

| クラス | 役割 |
|--------|------|
| `SensorDataStore` | `save(String): Long` / `readAll(): List<Pair<Long, String>>` / `delete(ids)` |
| `LocalFileStore` | JSONL を追記。シンプルだが行番号 ID なので並行書き込みに弱い |
| `SQLiteStore` | デフォルト実装。AUTOINCREMENT で安全な部分削除が可能 |

**`save()` が ID を返すのは store-and-forward の安全な削除のため。**
`readAll()` の途中で新データが書き込まれても、ID 指定の `delete()` なら未送信データを誤って消さない。

---

### network — データ送信

**責務**: 文字列ペイロードを外部へ送る。

| クラス | 役割 |
|--------|------|
| `DataSender` | `send(payload: String)` + ライフサイクル用 `onStop()` |
| `UdpSender` | UDP 送信（DatagramSocket） |
| `HttpSender` | HTTP POST 送信 |
| `FirestoreSender` | Cloud Firestore バッチ書き込み |

`DataSender` を実装するだけで MQTT・WebSocket など任意のプロトコルに対応できます。

---

### pipeline — 配線層

**責務**: sensing / storage / network を組み合わせる。

| クラス | 役割 |
|--------|------|
| `SensorDataSerializer` / `JsonSerializer` | `SensorData → String` 変換 |
| `SensorConsumer` | データ消費の統一インターフェース `onData(SensorData)` |
| `StoreConsumer` | Consumer → SensorDataStore へ非同期保存 |
| `SenderConsumer` | Consumer → DataSender へ非同期送信 |
| `SensorPipeline` | start/stop でフロー全体を制御 |
| `SyncJob` | Store 蓄積データを Sender へ一括転送（store-and-forward） |
| `SensorPipelineFactory` | 上記を組み立てるファクトリ |

---

## 重要な設計判断

### 1. なぜ `SensorConsumer` を統一インターフェースにしたか

最初は Pipeline が `store` と `sender` を直接保持する設計だった。
しかし「リアルタイム ML 推論」「複数の送信先」など、利用ケースが Sender/Store の二分法に収まらないと気づき、
**「Pipeline は SensorData を Consumer に配るだけ」** に責務を絞った。

Pipeline は Store/Sender を知らない。
StoreConsumer / SenderConsumer はそれぞれ独立したただの Consumer 実装になっている。

```
Pipeline
   ↓ onData(SensorData)
[Consumer A] [Consumer B] [Consumer C]
   ↓             ↓            ↓
  Store        Sender        ML
```

利用者が独自の `SensorConsumer` を追加するのも自由（例: スライディングウィンドウで推論）。

### 2. なぜ `SyncJob` を分離したか

Pipeline はリアルタイム収集の責務。Store からデータを読み出して送る作業（Store-and-Forward）は別フローなので、
**Pipeline からも Sender からも独立**した `SyncJob` クラスにした。

ネット接続時・WorkManager・手動など任意のタイミングで `execute()` を呼ぶ。失敗時は `delete()` せずに次回リトライ。

### 3. なぜ `SensorDataStore.save()` は ID を返すか

```
store.readAll() で ids=[1,2,3] を取得
    ↓ 送信中... この間に新データ ids=[4,5] が書き込まれる
store.delete([1,2,3])  ← 送信成功した分だけ削除
```

`clear()` で全件削除すると、送信中に来た新データも消してしまう。
ID 指定の `delete()` ならこの競合を回避できる。

### 4. なぜ `SensorPipelineFactory` を用意したか

`store` を `StoreConsumer` と `SyncJob` の両方で共有する必要があるため。
利用側で手動で組み立てると、複数箇所で同じ store インスタンスを渡し忘れるバグを起こしやすい。
ファクトリが共有 store を保持することで、組み立てミスを防ぐ。

### 5. `Service` をライブラリに含めない理由

Service は UI レイヤーの責務であり、ライブラリのスコープ外。
利用側アプリで薄いラッパー Service を書いて Pipeline を内部で使う前提にしている。

### 6. `BaseSensorCollector` のリスナーは `start()` で渡す

コンストラクタではなく `start(listener)` で渡す設計。
Pipeline が listener を組み立ててから渡せるため、Collector 単体の再利用性が高まる。

### 7. `SensorPipeline.stop()` の順序

```kotlin
fun stop() {
    collectors.forEach { it.stop() }                  // ① 新規イベント停止
    consumers.filterIsInstance<SenderConsumer>()
        .forEach { it.onStop() }                       // ② キュー drain → flush
}
```

`SenderConsumer.onStop()` は内部の executor を shutdown → awaitTermination してから
`sender.onStop()` を呼ぶ。これにより
「フラッシュ後にキュー済み send() が走ってバッファが復活」する race condition を防ぐ。
