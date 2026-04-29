# 033 SenderConsumer の実装

## 概要

受け取った SensorData を即時シリアライズして DataSender に渡す Consumer を実装する。
UDP・HTTP などリアルタイム送信用途に使用する。

## 前提

- 031 完了済みであること（`SensorConsumer` が存在する）

## 対象ファイル

新規作成: `pipeline/src/main/java/com/example/wearos/pipeline/SenderConsumer.kt`

## 実装内容

```kotlin
package com.example.wearos.pipeline

import com.example.wearos.network.DataSender
import com.example.wearos.sensing.SensorData

class SenderConsumer(
    private val sender: DataSender,
    private val serializer: SensorDataSerializer = JsonSerializer()
) : SensorConsumer {

    override fun onData(data: SensorData) {
        val serialized = serializer.serialize(data)
        sender.send(serialized)
    }

    fun onStop() {
        sender.onStop()
    }
}
```

## 完了条件

- `SenderConsumer.kt` が作成されている
- `SensorConsumer` を実装している
- ビルドが通ること
