package fr.stark.steauc.ble

import android.bluetooth.BluetoothGattCharacteristic
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup



class BLEService(
        title: String,
        items: List<BluetoothGattCharacteristic>
) : ExpandableGroup<BluetoothGattCharacteristic>(title, items)
