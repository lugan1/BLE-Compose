package com.softnet.blecompose.bluetooth.callback

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.softnet.blecompose.bluetooth.dto.ConnectionState
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
        MutableSharedFlow<List<BluetoothGattService>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        val state = ConnectionState.from(gatt, newState, status)
        onConnectionStateChange.tryEmit(state)
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        gatt?.run {
            onServiceDiscovered.tryEmit(services)
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) {
        onCharacteristicChanged.tryEmit(value)
    }
}
