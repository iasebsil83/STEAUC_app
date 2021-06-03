package fr.stark.steauc

import android.bluetooth.*
import android.content.Intent
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import fr.stark.steauc.ble.BLEScanActivity
import fr.stark.steauc.ble.BLEService
import fr.stark.steauc.ble.BLEServiceAdapter
import fr.stark.steauc.databinding.LyoSceneBinding
import fr.stark.steauc.gl.GLRenderer
import fr.stark.steauc.gl.XYZ






class SceneActivity : AppCompatActivity() {






    //BLE
    var BLEGatt      : BluetoothGatt? = null
    var receivedAccX : Double         = 0.0
    var receivedAccY : Double         = 0.0
    var receivedAccZ : Double         = 0.0
    var receivedGyrX : Double         = 0.0
    var receivedGyrY : Double         = 0.0
    var receivedGyrZ : Double         = 0.0

    //binding
    private lateinit var binding : LyoSceneBinding






    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        //CHECK RECEIVED INFO

        //get device info
        val device = intent.getParcelableExtra<BluetoothDevice>("BLEDevice")

        //go back to previous activity if incorrect info received
        if(device == null) {
            val intent = Intent(this, BLEScanActivity::class.java)
            startActivity(intent)
        }



        //CONNECTION

        //launch connection
        BLEGatt = device?.connectGatt(this, false, gattCallback)



        //LAYOUT

        //init binding instance
        binding = LyoSceneBinding.inflate(layoutInflater)



        //3D SCENE

        //init renderer
        GLRenderer.bindRenderer(this, binding.openglScene)

        //display
        setContentView(binding.root)
    }






    //EXPANDABLE RECYCLER VIEW

    //link all handlers with the adapter
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int){

            when(newState){
                BluetoothProfile.STATE_CONNECTED -> {
                    runOnUiThread {
                        binding.bleDeviceStatus.text = "Connected"
                    }
                    BLEGatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    runOnUiThread {
                        binding.bleDeviceStatus.text = "Disconnected"
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
                    }?.toMutableList() ?: arrayListOf(), this@SceneActivity
                )
                binding.bleServicesRecView.layoutManager = LinearLayoutManager(this@SceneActivity)
            }
        }
    }
}
