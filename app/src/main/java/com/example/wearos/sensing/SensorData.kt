package com.example.wearos.sensing

data class SensorData(
    val type: String,
    val values: FloatArray,
    val timestampNs: Long
) {
    companion object {
        const val TYPE_ACCELEROMETER = "accelerometer"
        const val TYPE_HEART_RATE    = "heart_rate"
        const val TYPE_LIGHT         = "light"
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
