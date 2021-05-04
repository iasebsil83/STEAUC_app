package fr.stark.steauc.ble

import android.bluetooth.*
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import fr.stark.steauc.R
import fr.stark.steauc.SceneActivity
import fr.stark.steauc.databinding.LyoBleInteractBinding
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message






class BLEInteractActivity : AppCompatActivity() {






    //debug info
    private val info : CodeInfo = CodeInfo("BLEInteract", "ble/BLEInteractActivity.kt")
    private val msg  : Message = Message(info)
    private val err  : Error   = Error  (info)

    //binding
    private lateinit var binding : LyoBleInteractBinding

    //BLE gatt
    var BLEGatt : BluetoothGatt? = null
    var status  : String         = "Status : unknown"






    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info.setFunctionName("onCreate")



        //CHECK RECEIVED INFO

        //get device info
        val device     = intent.getParcelableExtra<BluetoothDevice>("BLEDevice2")
        val deviceName = intent.getStringExtra("BLEDeviceName2")

        //go back to previous activity if incorrect info received
        if(device == null) {
            val intent = Intent(this, SceneActivity::class.java)
            startActivity(intent)
        }



        //LAYOUT

        //init binding instance
        binding = LyoBleInteractBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //display device info
        if(deviceName != null){
            findViewById<TextView>(R.id.ble_device_name).text = deviceName
        }

        //display connection status
        binding.bleDeviceStatus.text = getString(
                      R.string.ble_device_status,
            getString(R.string.ble_device_status_connecting)
        )



        //CONNECTION

        //launch connection
        BLEGatt = device?.connectGatt(this, false, gattCallback)
        //BLEGatt?.connect() //normally used only for reconnection
    }






    //EXPANDABLE RECYCLER VIEW

    //link all handlers with the adapter
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int){

            when(newState){
                BluetoothProfile.STATE_CONNECTED -> {
                    runOnUiThread {
                        binding.bleDeviceStatus.text = "Status : connected"
                    }
                    BLEGatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    runOnUiThread {
                        binding.bleDeviceStatus.text = "Status : disconnected"
                    }
                }
            }
        }

        override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }

        override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }
        override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            runOnUiThread {
                binding.bleServicesRecView.adapter = BLEServiceAdapter(
                    gatt,
                    gatt?.services?.map {
                        BLEService(
                            it.uuid.toString(),
                            it.characteristics
                        )
                    }?.toMutableList() ?: arrayListOf(), this@BLEInteractActivity
                )
                binding.bleServicesRecView.layoutManager = LinearLayoutManager(this@BLEInteractActivity)
            }
        }
    }
}
