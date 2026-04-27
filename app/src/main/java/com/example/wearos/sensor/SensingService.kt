package com.example.wearos.sensor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.wearos.network.UdpSender
import com.example.wearos.pipeline.SensorPipeline
import com.example.wearos.pipeline.SensorPipelineConfig
import com.example.wearos.sensing.AccelerometerCollector
import com.example.wearos.sensing.HeartRateCollector
import com.example.wearos.sensing.LightCollector

class SensingService : Service() {

    private lateinit var pipeline: SensorPipeline

    companion object {
        const val EXTRA_UDP_ADDRESS = "udp_address"
        const val EXTRA_UDP_PORT = "udp_port"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val address = intent?.getStringExtra(EXTRA_UDP_ADDRESS)
        val port = intent?.getIntExtra(EXTRA_UDP_PORT, -1)

        val sender = if (address != null && port != null && port != -1) {
            UdpSender(address, port)
        } else {
            null
        }

        val config = SensorPipelineConfig(
            collectors = listOf(
                AccelerometerCollector(this),
                HeartRateCollector(this),
                LightCollector(this)
            ),
            sender = sender
        )
        pipeline = SensorPipeline(config)
        pipeline.start()
        return START_STICKY
    }

    override fun onDestroy() {
        pipeline.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
