package com.softnet.blecompose.domain


interface CheckBluetooth {
    val isSupported: Boolean

    val isEnabled: Boolean

    //val bluetoothPermissions: List<Permission>

    val permissionGranted: Boolean
    fun beforeBluetoothFlow()
}
