package com.softnet.blecompose.bluetooth.specification.characteristic

enum class WriteType(val value: Int) {
    WITHOUT_RESPONSE(0x01), // Write without response
    DEFAULT(0x02), // Write request (or Write with response)
    RELIABLE_WRITE(0x03), // Prepared write (or Reliable write)
    SIGNED_WRITE(0x04); // Signed write without response

    companion object {
        fun from(value: Int): WriteType {
            return values().find { it.value == value } ?: throw IllegalArgumentException("Unknown value $value")
        }
    }
}
