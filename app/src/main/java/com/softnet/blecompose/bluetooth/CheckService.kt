package com.softnet.blecompose.bluetooth

interface CheckService {
    val isSupported: Boolean
    val isEnabled: Boolean
    val bluetoothPermissions: List<String>
    val permissionGranted: Boolean
    fun beforeBluetoothFlow()
}
