package com.softnet.blecompose.bluetooth.impl

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.softnet.blecompose.bluetooth.CheckService
import com.softnet.temperature.core.bluetooth.exception.DisabledException
import com.softnet.temperature.core.bluetooth.exception.PermissionException
import com.softnet.temperature.core.bluetooth.exception.UnsupportedException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CheckServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
): CheckService {
    private val adapter by lazy { context.getSystemService(BluetoothManager::class.java).adapter }

    override val isSupported: Boolean
        get() = adapter != null
    override val isEnabled: Boolean
        get() = adapter?.isEnabled ?: false

    override val bluetoothPermissions: List<String>
        get() = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        }
        else {
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    override val permissionGranted: Boolean
        get() = bluetoothPermissions.none { permission: String ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED
        }

    override fun beforeBluetoothFlow() {
        if(!isSupported) {
            throw UnsupportedException()
        }
        else if(!isEnabled) {
            throw DisabledException()
        }
        else if(!permissionGranted) {
            throw PermissionException()
        }
    }
}
