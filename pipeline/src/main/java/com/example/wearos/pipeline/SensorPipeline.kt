package com.example.wearos.pipeline

import com.example.wearos.sensing.SensorCollectorListener
import com.example.wearos.sensing.SensorData
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SensorPipeline(private val config: SensorPipelineConfig) {

    private val ioExecutor = Executors.newSingleThreadExecutor()
    private val sendExecutor = Executors.newSingleThreadExecutor()

    fun start() {
        val listener = object : SensorCollectorListener {
            override fun onSensorData(data: SensorData) {
                val serialized = config.serializer.serialize(data)
                ioExecutor.execute { config.store?.save(serialized) }
                sendExecutor.execute { config.sender?.send(serialized) }
            }
        }
        config.collectors.forEach { it.start(listener) }
    }

    fun stop() {
        config.collectors.forEach { it.stop() }
        // キュー済みの send() / save() を捌き切ってから onStop() でフラッシュする
        ioExecutor.shutdown()
        sendExecutor.shutdown()
        ioExecutor.awaitTermination(5, TimeUnit.SECONDS)
        sendExecutor.awaitTermination(5, TimeUnit.SECONDS)
        config.sender?.onStop()
    }
}
