package com.softnet.blecompose.bluetooth.dto

import android.annotation.SuppressLint
import android.bluetooth.le.*
import com.softnet.blecompose.bluetooth.callback.ScanCallbackImpl
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@SuppressLint("MissingPermission")
fun BluetoothLeScanner.scanAsFlow(
    beforeScanning: () -> Unit = {},
    filters: List<ScanFilter>,
    settings: ScanSettings,
    callback: ScanCallback = ScanCallbackImpl(),
    onScanClosed: () -> Unit = {}
): Flow<ScanResult> = callbackFlow {
    beforeScanning()

    startScan(filters, settings, callback)

    awaitClose {
        stopScan(callback)
        onScanClosed()
    }
}

@SuppressLint("MissingPermission")
fun BluetoothLeScanner.scanAsFlow(
    callback: ScanCallback = ScanCallbackImpl(),
    beforeScanning: () -> Unit = {},
    onScanClosed: () -> Unit = {}
): Flow<ScanResult> = callbackFlow {
    beforeScanning()

    startScan(callback)

    awaitClose {
        stopScan(callback)
        onScanClosed()
    }
}
