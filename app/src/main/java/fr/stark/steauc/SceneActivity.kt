package fr.stark.steauc

import android.bluetooth.*
import android.content.Intent
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import fr.stark.steauc.ble.BLEScanActivity
import fr.stark.steauc.ble.BLEService
import fr.stark.steauc.ble.BLEServiceAdapter
import fr.stark.steauc.databinding.LyoSceneBinding
import fr.stark.steauc.gl.*
import kotlin.math.PI

import fr.stark.steauc.gl.forms.Cuboid
import fr.stark.steauc.gl.forms.Plane



//movements
const val MOVING_STEP = 0.1f
const val ANGLE_STEP  = 0.1



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

    //scene elements
    private var sceneElements : MutableList<PlakObject> = mutableListOf()
    private lateinit var hand   : PlakObject
    private lateinit var cube1  : Cuboid
    private lateinit var cube2  : Cuboid
    private lateinit var ground : Plane




    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //BLE

        /* TEMPORARILY DISABLED
        //get device info
        val device = intent.getParcelableExtra<BluetoothDevice>("BLEDevice")

        //go back to previous activity if incorrect info received
        if(device == null){
            val intent = Intent(this, BLEScanActivity::class.java)
            startActivity(intent)
        }

        //launch connection
        BLEGatt = device?.connectGatt(this, false, gattCallback)
        */



        //LAYOUT

        //init binding instance
        binding = LyoSceneBinding.inflate(layoutInflater)
        GLRenderer.bindRenderer(this, binding.openglScene)
        setContentView(binding.root)



        //3D SCENE

        //moving buttons
        binding.sceneForward.setOnClickListener {
            for(e in 0 until sceneElements.size) {
                sceneElements[e].translate(0f, 0f, -MOVING_STEP)
            }
        }
        binding.sceneBackward.setOnClickListener {
            for(e in 0 until sceneElements.size) {
                sceneElements[e].translate(0f, 0f, MOVING_STEP)
            }
        }
        binding.sceneLeft.setOnClickListener{
            for(e in 0 until sceneElements.size){
                sceneElements[e].rotateY(-ANGLE_STEP)
            }
        }
        binding.sceneRight.setOnClickListener{
            for(e in 0 until sceneElements.size){
                sceneElements[e].rotateY(ANGLE_STEP)
            }
        }
        binding.sceneDown.setOnClickListener{
            for(e in 0 until sceneElements.size){
                sceneElements[e].rotateX(-ANGLE_STEP)
            }
        }
        binding.sceneUp.setOnClickListener{
            for(e in 0 until sceneElements.size){
                sceneElements[e].rotateX(ANGLE_STEP)
            }
        }

    }




    //GRAPHICAL SCENE
    fun initScene(){

        //hand
        hand = PlakObject(this, "scene_hand.stl", RED)
        hand.scale(10f, 10f, 10f, definitive=true)

        //cube1
        cube1  = Cuboid(100f, 100f, 100f, CYAN)
        cube1.translate(0f,-20f,100f, definitive=true)

        //cube2
        cube2  = Cuboid(90f, 90f, 100f, CYAN)
        cube2.translate(10f,-20f,200f, definitive=true)

        //ground
        ground = Plane(1000f,1000f, GREEN)
        ground.translate(0f,-100f,0f, definitive=true)

        //add elements to scene
        sceneElements.add(hand)
        sceneElements.add(cube1)
        sceneElements.add(cube2)
        sceneElements.add(ground)
    }
    fun updateScene(){
        //rotation
        //hand.resetPosition()
        //hand.rotate(0.0, PI, 0.0)
        /*hand.rotate(
            scene.receivedGyrX * PI/65536, // = PI/65536
            scene.receivedGyrY * PI/65536, //From [int16] to [-PI,PI]
            scene.receivedGyrZ * PI/65536
        )*/
        //hand.translate(0f, 0f, -0.09f)

        //debug
        //Log.i("Scene >","Update !")
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



    //getters
    fun getSceneElements() = sceneElements
}