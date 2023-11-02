package com.example.blecomposestudy.domain

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class GattCallbackImpl: BluetoothGattCallback() {
    val onConnectionStateChange =
        MutableSharedFlow<ConnectionState>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    val onCharacteristicChanged =
        MutableSharedFlow<ByteArray>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    val onServiceDiscovered =
        MutableSharedFlow<Unit>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        val state = ConnectionState.from(gatt, newState, status)
        onConnectionStateChange.tryEmit(state)
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        onServiceDiscovered.tryEmit(Unit)
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) {
        onCharacteristicChanged.tryEmit(value)
    }
}