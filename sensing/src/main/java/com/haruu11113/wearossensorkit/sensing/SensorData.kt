package com.haruu11113.wearossensorkit.sensing

data class SensorData(
    val type: String,
    val values: FloatArray,
    val timestampNs: Long
) {
    companion object {
        const val TYPE_ACCELEROMETER         = "accelerometer"
        const val TYPE_HEART_RATE            = "heart_rate"
        const val TYPE_LIGHT                 = "light"

        const val TYPE_GYROSCOPE             = "gyroscope"
        const val TYPE_MAGNETIC_FIELD        = "magnetic_field"
        const val TYPE_ROTATION_VECTOR       = "rotation_vector"
        const val TYPE_STEP_COUNTER          = "step_counter"
        const val TYPE_STEP_DETECTOR         = "step_detector"
        const val TYPE_GRAVITY               = "gravity"
        const val TYPE_LINEAR_ACCELERATION   = "linear_acceleration"
        const val TYPE_PRESSURE              = "pressure"
        const val TYPE_HEART_BEAT            = "heart_beat"
        const val TYPE_OXYGEN_SATURATION     = "oxygen_saturation"
        const val TYPE_SKIN_TEMPERATURE      = "skin_temperature"
        const val TYPE_OFF_BODY_DETECT       = "off_body_detect"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SensorData) return false
        return type == other.type &&
            values.contentEquals(other.values) &&
            timestampNs == other.timestampNs
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + values.contentHashCode()
        result = 31 * result + timestampNs.hashCode()
        return result
    }
}
