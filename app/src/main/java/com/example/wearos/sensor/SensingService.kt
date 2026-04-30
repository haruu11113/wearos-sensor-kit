package com.example.wearos.sensor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.wearos.network.UdpSender
import com.example.wearos.pipeline.SenderConsumer
import com.example.wearos.pipeline.SensorPipeline
import com.example.wearos.sensing.AccelerometerCollector
import com.example.wearos.sensing.GravityCollector
import com.example.wearos.sensing.GyroscopeCollector
import com.example.wearos.sensing.HeartBeatCollector
import com.example.wearos.sensing.HeartRateCollector
import com.example.wearos.sensing.LightCollector
import com.example.wearos.sensing.LinearAccelerationCollector
import com.example.wearos.sensing.MagneticFieldCollector
import com.example.wearos.sensing.OffBodyDetectCollector
import com.example.wearos.sensing.OxygenSaturationCollector
import com.example.wearos.sensing.PressureCollector
import com.example.wearos.sensing.RotationVectorCollector
import com.example.wearos.sensing.SkinTemperatureCollector
import com.example.wearos.sensing.StepCounterCollector
import com.example.wearos.sensing.StepDetectorCollector

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

        val collectors = listOf(
            AccelerometerCollector(this),
            GyroscopeCollector(this),
            MagneticFieldCollector(this),
            RotationVectorCollector(this),
            GravityCollector(this),
            LinearAccelerationCollector(this),
            StepCounterCollector(this),
            StepDetectorCollector(this),
            PressureCollector(this),
            HeartRateCollector(this),
            HeartBeatCollector(this),
            OxygenSaturationCollector(this),
            SkinTemperatureCollector(this),
            LightCollector(this),
            OffBodyDetectCollector(this)
        )
        val consumers = listOfNotNull(
            sender?.let { SenderConsumer(it) }
        )
        pipeline = SensorPipeline(collectors, consumers)
        pipeline.start()
        return START_STICKY
    }

    override fun onDestroy() {
        pipeline.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
