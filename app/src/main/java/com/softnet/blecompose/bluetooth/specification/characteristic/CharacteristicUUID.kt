package com.softnet.blecompose.bluetooth.specification.characteristic

enum class CharacteristicUUID(val uuid: String) {
    // Bluetooth SIG
    DEVICE_NAME("00002a00-0000-1000-8000-00805f9b34fb"),
    APPEARANCE("00002a01-0000-1000-8000-00805f9b34fb"),
    PERIPHERAL_PRIVACY_FLAG("00002a02-0000-1000-8000-00805f9b34fb"),
    RECONNECTION_ADDRESS("00002a03-0000-1000-8000-00805f9b34fb"),
    PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS("00002a04-0000-1000-8000-00805f9b34fb"),
    CENTRAL_ADDRESS_RESOLUTION("00002aa6-0000-1000-8000-00805f9b34fb"),
    SERVICE_CHANGED("00002a05-0000-1000-8000-00805f9b34fb"),
    ALERT_LEVEL("00002a06-0000-1000-8000-00805f9b34fb"),
    TX_POWER_LEVEL("00002a07-0000-1000-8000-00805f9b34fb"),
    DATE_TIME("00002a08-0000-1000-8000-00805f9b34fb"),
    DAY_OF_WEEK("00002a09-0000-1000-8000-00805f9b34fb"),
    DAY_DATE_TIME("00002a0a-0000-1000-8000-00805f9b34fb"),
    EXACT_TIME_256("00002a0c-0000-1000-8000-00805f9b34fb"),
    DST_OFFSET("00002a0d-0000-1000-8000-00805f9b34fb"),
    TIME_ZONE("00002a0e-0000-1000-8000-00805f9b34fb"),
    LOCAL_TIME_INFORMATION("00002a0f-0000-1000-8000-00805f9b34fb"),
    TIME_WITH_DST("00002a11-0000-1000-8000-00805f9b34fb"),
    TIME_ACCURACY("00002a12-0000-1000-8000-00805f9b34fb"),
    TIME_SOURCE("00002a13-0000-1000-8000-00805f9b34fb"),
    REFERENCE_TIME_INFORMATION("00002a14-0000-1000-8000-00805f9b34fb"),
    TIME_UPDATE_CONTROL_POINT("00002a16-0000-1000-8000-00805f9b34fb"),
    TIME_UPDATE_STATE("00002a17-0000-1000-8000-00805f9b34fb"),
    GLUCOSE_MEASUREMENT("00002a18-0000-1000-8000-00805f9b34fb"),
    BATTERY_LEVEL("00002a19-0000-1000-8000-00805f9b34fb"),
    TEMPERATURE_MEASUREMENT("00002a1c-0000-1000-8000-00805f9b34fb"),
    TEMPERATURE_TYPE("00002a1d-0000-1000-8000-00805f9b34fb"),
    INTERMEDIATE_TEMPERATURE("00002a1e-0000-1000-8000-00805f9b34fb"),
    MEASUREMENT_INTERVAL("00002a21-0000-1000-8000-00805f9b34fb"),
    BOOT_KEYBOARD_INPUT_REPORT("00002a22-0000-1000-8000-00805f9b34fb"),
    SYSTEM_ID("00002a23-0000-1000-8000-00805f9b34fb"),
    MODEL_NUMBER_STRING("00002a24-0000-1000-8000-00805f9b34fb"),
    SERIAL_NUMBER_STRING("00002a25-0000-1000-8000-00805f9b34fb"),
    FIRMWARE_REVISION_STRING("00002a26-0000-1000-8000-00805f9b34fb"),
    HARDWARE_REVISION_STRING("00002a27-0000-1000-8000-00805f9b34fb"),

    // Nordic Semiconductor
    BUTTON_LESS_DFU("8ec90003-f315-4f60-9fb8-838830daea50"),
    TX_CHARACTERISTIC("6e400002-b5a3-f393-e0a9-e50e24dcca9e"),
    RX_CHARACTERISTIC("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    companion object {
        fun from(uuid: String): CharacteristicUUID {
            return values().find { it.uuid == uuid } ?: throw IllegalArgumentException("Unknown UUID $uuid")
        }
    }
}