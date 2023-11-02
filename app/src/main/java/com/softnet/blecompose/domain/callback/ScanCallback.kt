package com.softnet.temperature.core.bluetooth.callback

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import kotlinx.coroutines.channels.ProducerScope

fun ProducerScope<ScanResult>.scanCallback(): ScanCallback = object : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        trySend(result).isSuccess
    }
}
