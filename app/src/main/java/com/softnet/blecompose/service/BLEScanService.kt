package com.softnet.blecompose.service

import android.app.Service
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.softnet.blecompose.bluetooth.ScanService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@AndroidEntryPoint
class BLEScanService : Service(), ScanService {
    @Inject lateinit var scanService: ScanService

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): BLEScanService = this@BLEScanService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override val isScanning: Boolean
        get() = scanService.isScanning

    override fun startScan(): Flow<ScanResult> = scanService.startScan()

    override fun startScan(filters: List<ScanFilter>, settings: ScanSettings): Flow<ScanResult> = scanService.startScan(filters, settings)
    override fun stopScan() = scanService.stopScan()
}