package fr.stark.steauc

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import fr.stark.steauc.ble.BLEService
import fr.stark.steauc.ble.BLEServiceAdapter
import fr.stark.steauc.databinding.LyoSceneBinding
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.gl.*



//movements
const val MOVING_STEP = 0.001f
const val ANGLE_STEP  = 0.05f

const val HAND   = 0
const val CUBE1  = 1
const val CUBE2  = 2
const val GROUND = 3



class SceneActivity : AppCompatActivity() {

    //debug info
    private val info : CodeInfo = CodeInfo("Main", "SceneActivity.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

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

    //camera
    private val camPos = XYZ()
    private val camRot = XYZ()
    private val camSca = XYZ()




    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info.setFunctionName("onCreate")

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
            for(e in sceneElements) {
                e.translate(0f, 0f, -MOVING_STEP)
            }
            camPos.z -= MOVING_STEP
            logState()
        }
        binding.sceneBackward.setOnClickListener {
            for(e in sceneElements) {
                e.translate(0f, 0f, MOVING_STEP)
            }
            camPos.z += MOVING_STEP
            logState()
        }
        binding.sceneLeft.setOnClickListener{
            for(e in sceneElements){
                e.rotateY(-ANGLE_STEP)
            }
            camRot.y -= ANGLE_STEP
            logState()
        }
        binding.sceneRight.setOnClickListener{
            for(e in sceneElements){
                e.rotateY(ANGLE_STEP)
            }
            camRot.y += ANGLE_STEP
            logState()
        }
        binding.sceneDown.setOnClickListener{
            for(e in sceneElements){
                e.rotateX(-ANGLE_STEP)
            }
            camRot.x -= ANGLE_STEP
            logState()
        }
        binding.sceneUp.setOnClickListener{
            for(e in sceneElements){
                e.rotateX(ANGLE_STEP)
            }
            camRot.x += ANGLE_STEP
            logState()
        }

    }




    //GRAPHICAL SCENE
    fun initScene(){
        info.setFunctionName("initScene")

        //hand
        //sceneElements.add(PlakObject( Utils.readSTL(this, "scene_hand.stl"), RED))
        //sceneElements[0].scale(0.001f, 0.001f, 0.001f, definitive=true)

        //cube1
        //sceneElements.add(PlakObject( Forms.Hexaedron(0.2f, 0.2f, 0.2f), CYAN))
        //sceneElements[1].scale(0.001f,0.001f,0.001f, definitive=true)

        //cube2
        sceneElements.add(PlakObject( Forms.Hexaedron(0.1f, 0.1f, 0.1f), CYAN))
        sceneElements[0].translate(0f,0.01f,0f, definitive=true)

        //ground
        sceneElements.add(PlakObject( Forms.Plane(2f,2f), GREEN))
        sceneElements[1].translate(-1f,0f,0f, definitive=true)
    }

    fun updateScene(){
        info.setFunctionName("updateScene")

        //reset position

        //rotation
        camRot.y += ANGLE_STEP
        sceneElements.forEach { e ->
            e.resetPosition()
            e.rotate(camRot.x, camRot.y, camRot.z)
        }

        //debug
        //msg.log("Update !")
    }




    //EXPANDABLE RECYCLER VIEW

    //link all handlers with the adapter
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int){
            info.setFunctionName("onConnectionStateChange")

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

        override fun onCharacteristicRead(gatt:BluetoothGatt?, characteristic:BluetoothGattCharacteristic?, status:Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            info.setFunctionName("onCharacteristicRead")

            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }

        override fun onCharacteristicWrite(gatt:BluetoothGatt?, characteristic:BluetoothGattCharacteristic?, status:Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            info.setFunctionName("onCharacteristicWrite")

            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }
        override fun onCharacteristicChanged(gatt:BluetoothGatt, characteristic:BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            info.setFunctionName("onCharacteristicChanged")

            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            info.setFunctionName("onServicesDiscovered")

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

    fun logState() {
        msg.log("Pos${camPos.print()}, Rot${camRot.print()}")
    }



    //getters
    fun getSceneElements() = sceneElements
    fun getPosition()      = camPos
    fun getRotation()      = camRot
    fun getScale()         = camSca
}
