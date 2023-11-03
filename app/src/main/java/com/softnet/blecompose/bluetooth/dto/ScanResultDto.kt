package com.softnet.blecompose.bluetooth.dto

data class ScanResultDto(
    val name: String = "Unknown",
    val macAddress: String,
    val rssi: Int = 0,
    val bytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanResultDto

        if (name != other.name) return false
        if (macAddress != other.macAddress) return false
        if (rssi != other.rssi) return false
        if (bytes != null) {
            if (other.bytes == null) return false
            if (!bytes.contentEquals(other.bytes)) return false
        } else if (other.bytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + macAddress.hashCode()
        result = 31 * result + rssi
        result = 31 * result + (bytes?.contentHashCode() ?: 0)
        return result
    }
}