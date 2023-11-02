package com.softnet.blecompose.domain.dto

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt

sealed interface ConnectionState {
    object Connecting: ConnectionState
    object Connected: ConnectionState
    object Disconnecting: ConnectionState
    object Disconnected: ConnectionState
    object UnlikelyError: ConnectionState
    object ConnectionFail: ConnectionState
    object ConnectFailedTime: ConnectionState
    data class ConnectFailedUnknown(val status: Int): ConnectionState
    data class UnknownState(val newState: Int, val status: Int): ConnectionState

    companion object {
        @SuppressLint("MissingPermission")
        fun from(gatt: BluetoothGatt?, newState: Int, status: Int): ConnectionState {
            return when(newState) {
                BluetoothGatt.STATE_CONNECTING -> Connecting
                BluetoothGatt.STATE_CONNECTED -> Connected
                BluetoothGatt.STATE_DISCONNECTING -> Disconnecting
                BluetoothGatt.STATE_DISCONNECTED -> {
                    gatt?.close()
                    when(status) {
                        0 -> Disconnected
                        8 -> ConnectFailedTime
                        14 -> UnlikelyError
                        133 -> ConnectionFail
                        else -> ConnectFailedUnknown(status)
                    }
                }
                else -> UnknownState(newState, status)
            }
        }
    }
}
