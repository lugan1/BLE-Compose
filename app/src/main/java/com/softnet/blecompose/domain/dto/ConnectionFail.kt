package com.softnet.temperature.core.bluetooth.dto

sealed interface ConnectionFail {
    data object UNLIKELY_ERROR : ConnectionFail

    data object CONNECT_FAILED : ConnectionFail

    data object CONNECT_FAILED_TIMEOUT : ConnectionFail

    data object DISCONNECTED : ConnectionFail

    data object CONNECT_FAILED_UNKNOWN : ConnectionFail
}