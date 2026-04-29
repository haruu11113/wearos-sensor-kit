# 034 SensorPipeline のリファクタリング

## 概要

`SensorPipeline` を `SensorPipelineConfig` を廃止し、
`collectors` と `consumers` を直接受け取る形に変更する。
Pipeline は SensorData を Consumer に配布するだけの責務に絞る。

## 前提

- 031 完了済みであること（`SensorConsumer` が存在する）
- 032 完了済みであること（`StoreConsumer` が存在する）
- 033 完了済みであること（`SenderConsumer` が存在する）

## 対象ファイル

変更:
- `pipeline/src/main/java/com/example/wearos/pipeline/SensorPipeline.kt`

削除:
- `pipeline/src/main/java/com/example/wearos/pipeline/SensorPipelineConfig.kt`

## 変更後の SensorPipeline

```kotlin
package com.example.wearos.pipeline

import com.example.wearos.sensing.BaseSensorCollector
import com.example.wearos.sensing.SensorCollectorListener
import com.example.wearos.sensing.SensorData

class SensorPipeline(
    private val collectors: List<BaseSensorCollector>,
    private val consumers:  List<SensorConsumer>
) {
    fun start() {
        val listener = object : SensorCollectorListener {
            override fun onSensorData(data: SensorData) {
                consumers.forEach { it.onData(data) }
            }
        }
        collectors.forEach { it.start(listener) }
    }

    fun stop() {
        collectors.forEach { it.stop() }
        consumers.filterIsInstance<SenderConsumer>().forEach { it.onStop() }
    }
}
```

## 注意事項

- `SensorPipelineConfig.kt` は削除する
- `app` モジュール内で `SensorPipelineConfig` を使っている箇所があれば合わせて修正する
- `stop()` 内で `SenderConsumer.onStop()` を呼ぶことで、バッファを持つ Sender のフラッシュを保証する

## 完了条件

- `SensorPipeline` が `collectors` + `consumers` を直接受け取る形になっている
- `SensorPipelineConfig.kt` が削除されている
- ビルドが通ること
