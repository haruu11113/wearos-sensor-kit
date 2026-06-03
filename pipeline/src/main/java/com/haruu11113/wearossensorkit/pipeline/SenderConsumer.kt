package com.haruu11113.wearossensorkit.pipeline

import com.haruu11113.wearossensorkit.network.DataSender
import com.haruu11113.wearossensorkit.sensing.SensorData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SenderConsumer(
    private val sender: DataSender,
    private val serializer: SensorDataSerializer = JsonSerializer(),
    private val executor: ExecutorService = Executors.newSingleThreadExecutor(),
) : SensorConsumer {

    override fun onData(data: SensorData) {
        val serialized = serializer.serialize(data)
        executor.execute { sender.send(serialized) }
    }

    override fun onStop() {
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
        sender.onStop()
    }
}
