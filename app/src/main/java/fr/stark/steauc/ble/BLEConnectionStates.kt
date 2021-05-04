package fr.stark.steauc.ble

import android.bluetooth.BluetoothProfile
import androidx.annotation.StringRes
import fr.stark.steauc.R



enum class BLEConnectionStates(val state: Int, @StringRes val text : Int){

        STATE_CONNECTING   (BluetoothProfile.STATE_CONNECTING,    R.string.ble_device_status_connecting   ),
        STATE_CONNECTED    (BluetoothProfile.STATE_CONNECTED,     R.string.ble_device_status_connected    ),
        STATE_DISCONNECTED (BluetoothProfile.STATE_DISCONNECTED,  R.string.ble_device_status_disconnected ),
        STATE_DISCONNECTING(BluetoothProfile.STATE_DISCONNECTING, R.string.ble_device_status_disconnecting);

        companion object {
            fun getBLEConnectionStateFromState(state: Int) = values().firstOrNull(){
                it.state == state
            }
        }
}