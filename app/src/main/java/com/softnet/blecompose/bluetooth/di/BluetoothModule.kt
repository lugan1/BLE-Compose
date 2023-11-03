package com.softnet.blecompose.bluetooth.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.softnet.blecompose.bluetooth.BluetoothLeService
import com.softnet.blecompose.bluetooth.CheckService
import com.softnet.blecompose.bluetooth.ScanService
import com.softnet.blecompose.bluetooth.callback.GattCallbackImpl
import com.softnet.blecompose.bluetooth.impl.BluetoothLeServiceImpl
import com.softnet.blecompose.bluetooth.impl.CheckServiceImpl
import com.softnet.blecompose.bluetooth.impl.ScanServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {
    @Singleton
    @Provides
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager {
        return context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    @Singleton
    @Provides
    fun provideAdapter(bluetoothManager: BluetoothManager): BluetoothAdapter? {
        return bluetoothManager.adapter
    }

    @Singleton
    @Provides
    fun provideCheckService(@ApplicationContext context: Context): CheckService {
        return CheckServiceImpl(context)
    }

    @Singleton
    @Provides
    fun provideScanService(
        checkService: CheckService,
        bluetoothManager: BluetoothManager
    ): ScanService {
        return ScanServiceImpl(
            check = checkService,
            scanner = bluetoothManager.adapter.bluetoothLeScanner
        )
    }

    @Singleton
    @Provides
    fun provideGattCallback(): GattCallbackImpl = GattCallbackImpl()

    @Singleton
    @Provides
    fun provideBluetoothLeService(
        @ApplicationContext context: Context,
        checkService: CheckService,
        adapter: BluetoothAdapter?,
        callback: GattCallbackImpl
    ): BluetoothLeService {
        return BluetoothLeServiceImpl(context, checkService, adapter, callback)
    }
}
