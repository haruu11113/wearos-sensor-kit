# WearOS センサーストリーミングアプリ

Wear OS デバイス（スマートウォッチ）でセンサーデータを取得し、UDP で外部サーバーへリアルタイム送信するアプリケーションです。

## 概要

| 項目 | 内容 |
|------|------|
| プラットフォーム | Wear OS (minSdk 30 / targetSdk 33) |
| 言語 | Kotlin |
| UI フレームワーク | Jetpack Compose (Wear Material) |
| 通信プロトコル | UDP |

## 機能

- 加速度センサー・心拍数センサー・照度センサーのデータ取得
- センサーデータを CSV 形式で UDP パケットとして送信
- Start / Stop ボタンによるセンシングの制御
- `BODY_SENSORS` パーミッションの実行時リクエスト

## プロジェクト構成

```
app/src/main/java/com/example/wearos/
├── network/
│   ├── BaseSender.kt               # 送信インターフェース
│   └── UdpSender.kt                # UDP 送信実装
├── sensor/
│   ├── BaseSensorService.kt        # センサー Service の抽象基底クラス
│   ├── AccelerometerSensorService.kt  # 加速度センサー
│   ├── HeartRateSensorService.kt      # 心拍数センサー
│   └── LightSensorService.kt          # 照度センサー
├── presentation/
│   ├── MainActivity.kt             # メイン画面・Service 制御
│   └── theme/                      # Compose テーマ (Color / Type / Theme)
├── tile/
│   └── MainTileService.kt          # Wear OS タイル
└── complication/
    └── MainComplicationService.kt  # ウォッチフェース コンプリケーション
```

## センサーとデータ形式

| センサー | 更新レート | 送信フォーマット |
|----------|------------|-----------------|
| 加速度計 | SENSOR_DELAY_GAME | `acc,X,Y,Z,timestamp` |
| 心拍数 | SENSOR_DELAY_GAME | `heart_rate,value,timestamp` |
| 照度 | SENSOR_DELAY_NORMAL | `timestamp,light,value` |

## UDP 送信先

`UdpSender.kt` でデフォルトの IP・ポートを指定しています。

```kotlin
// app/src/main/java/com/example/wearos/network/UdpSender.kt
private val host = "192.168.50.78"
private val port = 6666
```

受信側でポート `6666` を listen しておくとデータを受け取れます。

```bash
# 受信確認例 (Linux/macOS)
nc -ulp 6666
```

## パーミッション

`AndroidManifest.xml` で宣言されている主なパーミッション:

| パーミッション | 用途 |
|----------------|------|
| `BODY_SENSORS` | 心拍数センサーへのアクセス（実行時リクエスト） |
| `BODY_SENSORS_BACKGROUND` | バックグラウンドでの心拍数取得 |
| `INTERNET` | UDP 送信 |
| `WAKE_LOCK` | センシング中のスリープ抑制 |

## ビルド・実行方法

1. Android Studio で本プロジェクトを開く
2. Wear OS 実機またはエミュレーターをデバイスとして選択
3. Run ▶ でインストール
4. アプリを起動 → パーミッション許可 → **Start** をタップ

## アーキテクチャ概要

```
MainActivity
  └─ Start/Stop ──▶ AccelerometerSensorService
                     HeartRateSensorService
                     LightSensorService
                       └─ onSensorChanged()
                            └─ BaseSensorService.formatMessage()
                                 └─ UdpSender.sendMessage()  ──▶ UDP パケット送信
```

センサーイベントはバックグラウンドスレッドで UDP 送信されるため、メインスレッドをブロックしません。

## ドキュメント

- [Android Service / Activity の説明](docs/README.md)
