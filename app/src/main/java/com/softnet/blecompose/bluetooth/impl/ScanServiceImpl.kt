package com.softnet.blecompose.bluetooth.impl

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.softnet.blecompose.bluetooth.CheckService
import com.softnet.blecompose.bluetooth.ScanService
import com.softnet.blecompose.bluetooth.callback.ScanCallbackImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanServiceImpl @Inject constructor(
    private val check: CheckService,
    private val scanner: BluetoothLeScanner?,
    private val callback: ScanCallbackImpl,
): ScanService {
    override var isScanning: Boolean = false

    @SuppressLint("MissingPermission")
    override fun startScan(): Flow<ScanResult> {
        check.beforeBluetoothFlow()
        scanner?.startScan(callback)
        return callback.onScanResult
    }
    @SuppressLint("MissingPermission")
    override fun startScan(filters: List<ScanFilter>, settings: ScanSettings): Flow<ScanResult> {
        check.beforeBluetoothFlow()
        scanner?.startScan(filters, settings, callback)
        return callback.onScanResult
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        check.beforeBluetoothFlow()
        scanner?.stopScan(callback)
    }


}