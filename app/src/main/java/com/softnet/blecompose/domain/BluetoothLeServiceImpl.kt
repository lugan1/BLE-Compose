package com.softnet.blecompose.domain

import android.Manifest
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.blecomposestudy.domain.BluetoothLeService
import com.example.blecomposestudy.domain.ConnectionState
import com.example.blecomposestudy.domain.GattCallbackImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.util.UUID

class BluetoothLeServiceImpl : Service(), BluetoothLeService {
    private var gatt: BluetoothGatt? = null
    private var manager: BluetoothManager? = null
    private var adapter: BluetoothAdapter? = null
    private val binder = LocalBinder()

    private val gattCallback = GattCallbackImpl()

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeServiceImpl {
            return this@BluetoothLeServiceImpl
        }
    }

    override fun onBind(intent: Intent): IBinder {
        initialize()
        return binder
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        disconnect()
    }

    private fun initialize(): Boolean {
        manager = getSystemService(BluetoothManager::class.java)
        adapter = manager?.adapter

        if(adapter == null) {
            // Unable to obtain a BluetoothAdapter.
            return false
        }

        return true
    }

    override fun connect(macAddress: String): Flow<ConnectionState> {
        if(adapter == null) {
            throw Exception("BluetoothAdapter is null")
        }

        adapter?.getRemoteDevice(macAddress)?.let { device ->
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                throw Exception("Permission not granted")
            }
            gatt = device.connectGatt(this, false, gattCallback)
        }

        return gattCallback.onConnectionStateChange
    }

    override fun connect(device: BluetoothDevice): Flow<ConnectionState> {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }
        if(gatt != null) {
            throw Exception("Gatt is not null")
        }

        gatt = device.connectGatt(this, false, gattCallback)
        return gattCallback.onConnectionStateChange
    }

    override fun discoverServices(): Flow<Unit> {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }
        if(gatt == null) {
            throw Exception("Gatt is null")
        }

        gatt?.discoverServices()
        return gattCallback.onServiceDiscovered
    }

    override fun onConnectionStateChange(): Flow<ConnectionState> {
        return gattCallback.onConnectionStateChange
    }

    override fun disconnect() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        gatt?.disconnect()
    }

    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: ByteArray
    ): Flow<ByteArray> {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }
        if(gatt == null) {
            throw Exception("Gatt is null")
        }

        gatt?.run {
            val service = getService(serviceUUID)
            val characteristic = service.getCharacteristic(characteristicUUID)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                writeCharacteristic(characteristic, data, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            }
            else {
                characteristic.value = data
                writeCharacteristic(characteristic)
            }
        }

        return gattCallback.onCharacteristicChanged
    }

    override fun characteristicNotification(serviceUUID: UUID, characteristicUUID: UUID, descriptorUUID: UUID, value: Boolean): Flow<Unit> = flow {
        if (ActivityCompat.checkSelfPermission(this@BluetoothLeServiceImpl, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw Exception("Permission not granted")
        }

        gatt?.run {
            val service = getService(serviceUUID)
            val characteristic = service.getCharacteristic(characteristicUUID)
            val descriptor = characteristic.getDescriptor(descriptorUUID)
            setCharacteristicNotification(characteristic, value)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            }
            else {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                writeDescriptor(descriptor)
            }
        }
        emit(Unit)
    }
}