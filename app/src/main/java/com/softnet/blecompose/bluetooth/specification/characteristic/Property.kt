package com.softnet.blecompose.bluetooth.specification.characteristic

enum class Property(val value: Int) {
    // https://www.bluetooth.com/specifications/gatt/characteristics/
    NONE(0x00),
    BROADCAST(0x01),
    READ(0x02),
    WRITE_NO_RESPONSE(0x04),
    WRITE(0x08),
    NOTIFY(0x10),
    INDICATE(0x20),
    SIGNED_WRITE(0x40),
    EXTENDED_PROPS(0x80);

    companion object {
        fun from(value: Int): List<Property> {
            return values().filter { it.value and value != 0 }
        }
    }
}