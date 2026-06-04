# wearos-sensor-kit

[![JitPack](https://jitpack.io/v/haruu11113/wearos-sensor-kit.svg)](https://jitpack.io/#haruu11113/wearos-sensor-kit)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Wear OS デバイス向けセンサーデータ収集ライブラリ。15 種類のセンサーから値を取得し、
JSON 形式でローカル保存・UDP / HTTP / Firestore 送信ができます。
センシング・保存・送信が疎結合に分離されており、組み合わせ自由です。

---

## 対応範囲

| プラットフォーム | 対応 |
|----------------|------|
| Wear OS 3.x 以降（API 30+） | ✅ 主対象（動作確認: Pixel Watch 2） |
| Wear OS 4 (API 34+) | ✅ `health.READ_HEART_RATE` 対応済み |
| Android スマートフォン | ⚠️ 心拍・SpO2・皮膚温度等のセンサーは動作不可（ハードウェア非搭載） |

| センサー | Pixel Watch 2 | 他 Wear OS |
|---------|---------------|-----------|
| 加速度・ジャイロ・地磁気・回転ベクトル | ✅ | ✅ |
| 重力・線形加速度・歩数・歩行検出・気圧・照度 | ✅ | ✅ |
| 心拍数・心拍ビート | ✅ | ✅（要 `BODY_SENSORS`） |
| SpO2（血中酸素飽和度） | ✅ | デバイス次第 |
| 皮膚温度 | ✅ | Pixel Watch 2 固有想定 |
| 装着検出（off-body） | ✅ | ✅ |

センサーが存在しないデバイスでは黙ってスキップされるので、安全に部分利用できます。

---

## インストール

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

// app/build.gradle.kts
dependencies {
    implementation("com.github.haruu11113.wearos-sensor-kit:pipeline:1.0.1")
}
```

`pipeline` への依存だけで `sensing` / `storage` / `network` も自動で取得されます。

詳細は [docs/USAGE.md](docs/USAGE.md) 参照。

---

## 最小コード例

```kotlin
class MySensingService : Service() {
    private lateinit var pipeline: SensorPipeline

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val factory = SensorPipelineFactory(this)
        pipeline = factory.buildPipeline(
            collectors = listOf(AccelerometerCollector(this), HeartRateCollector(this)),
            consumers  = listOf(factory.senderConsumer(UdpSender("192.168.1.100", 6666)))
        )
        pipeline.start()
        return START_STICKY
    }

    override fun onDestroy() {
        pipeline.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
```

`AndroidManifest.xml` に必要なパーミッション:

```xml
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.health.READ_HEART_RATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

Store-and-Forward・Firestore・独自 Consumer など → [docs/USAGE.md](docs/USAGE.md)

---

## モジュール構成

```
sensing   ─▶ センサー取得（15種類）
storage   ─▶ ローカル保存（SQLite / JSONL）
network   ─▶ 送信（UDP / HTTP / Firestore）
pipeline  ─▶ 上記を組み合わせる配線層
```

設計判断の詳細 → [docs/DESIGN.md](docs/DESIGN.md)

---

## ドキュメント

- [使い方ガイド](docs/USAGE.md) — インストール・コード例・パーミッション
- [設計ドキュメント](docs/DESIGN.md) — アーキテクチャ・設計判断
- [ライセンス](LICENSE) — MIT License

---

## ライセンス

MIT License. 詳細は [LICENSE](LICENSE) を参照。
