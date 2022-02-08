package fr.stark.steauc

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import fr.stark.steauc.ble.BLEService
import fr.stark.steauc.ble.BLEServiceAdapter
import fr.stark.steauc.databinding.LyoSceneBinding
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.gl.*



//scene refresh
const val UPDATE_SCENE_DELAY : Long = 100 //in ms

// centralization
val NEW_CENTER  = XYZ(-0.5f, -0.5f, 0f)

//scene elements
const val CUBE        = 0
const val FINGER_END  = 1
const val FINGER_MID  = 2
const val FINGER_BASE = 3
const val HAND        = 4
const val FULL_HAND   = 5
const val GROUND      = 6

//actions
const val TRANSLATE = 0
const val ROTATE    = 1
const val SCALE     = 2

//action steps
const val TRANS_STEP  = 0.02f
const val ANGLE_STEP  = 0.08f
const val SCALE_STEP  = 1.05f




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

    //selection
    private var selector      = 0
    private val ELEMENT_NAMES = listOf(
        "CUBE",
        "FINGER_END",
        "FINGER_MID",
        "FINGER_BASE",
        "HAND",
        "FULL_HAND",
        "GROUND"
    )

    //actions
    private var action       = 0
    private val ACTION_NAMES = listOf(
        "TRANSLATE",
        "ROTATE",
        "SCALE"
    )
    private var scales = mutableListOf<Float>()




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

        //init default texts
        binding.sceneSelect.text = ELEMENT_NAMES[0]
        binding.sceneAction.text = ACTION_NAMES[0]



        //3D SCENE

        //target movement
        binding.sceneSelect.setOnClickListener {
            selector++
            if(selector >= 5){
                selector = 0
            }
            binding.sceneSelect.text = ELEMENT_NAMES[selector]
        }
        binding.sceneAction.setOnClickListener {
            action++
            if(action >= 3){
                action = 0
            }
            binding.sceneAction.text = ACTION_NAMES[action]
        }

        //execute movement
        binding.sceneLeft.setOnClickListener{
            when(action) {
                TRANSLATE -> sceneElements[selector].translateX(-TRANS_STEP)
                ROTATE    -> sceneElements[selector].rotateY( sceneElements[selector].getPosition(), -ANGLE_STEP)
                SCALE     -> sceneElements[selector].scaleX(1f/SCALE_STEP)
            }
        }
        binding.sceneRight.setOnClickListener{
            when(action) {
                TRANSLATE -> sceneElements[selector].translateX(TRANS_STEP)
                ROTATE    -> sceneElements[selector].rotateY( sceneElements[selector].getPosition(), ANGLE_STEP)
                SCALE     -> sceneElements[selector].scaleX(SCALE_STEP)
            }
        }
        binding.sceneDown.setOnClickListener{
            when(action) {
                TRANSLATE -> sceneElements[selector].translateY(-TRANS_STEP)
                ROTATE    -> sceneElements[selector].rotateX( sceneElements[selector].getPosition(), -ANGLE_STEP)
                SCALE     -> sceneElements[selector].scaleY(1/SCALE_STEP)
            }
        }
        binding.sceneUp.setOnClickListener{
            when(action) {
                TRANSLATE -> sceneElements[selector].translateY(TRANS_STEP)
                ROTATE    -> sceneElements[selector].rotateX( sceneElements[selector].getPosition(), ANGLE_STEP)
                SCALE     -> sceneElements[selector].scaleY(SCALE_STEP)
            }
        }

    }




    //GRAPHICAL SCENE
    fun initScene() : Long {
        info.setFunctionName("initScene")

        //cube
        sceneElements.add(PlakObject( Forms.Hexaedron(0.1f, 0.1f, 0.1f), CYAN))
        sceneElements[CUBE].translate(-0.5f,-0.5f,0f, definitive=true)

        //finger end
        sceneElements.add(PlakObject( Utils.readSTL(this, "finger_end.stl"), BLUE))
        sceneElements[FINGER_END].scale(0.001f, 0.001f, 0.001f, definitive=true)
        sceneElements[FINGER_END].translate(-0.5f,-0.5f,0f, definitive=true)

        //finger mid
        sceneElements.add(PlakObject( Utils.readSTL(this, "finger_mid.stl"), MAGENTA))
        sceneElements[FINGER_MID].scale(0.001f, 0.001f, 0.001f, definitive=true)
        sceneElements[FINGER_MID].translate(-0.5f,-0.5f,0f, definitive=true)

        //finger base
        sceneElements.add(PlakObject( Utils.readSTL(this, "finger_base.stl"), RED))
        sceneElements[FINGER_BASE].scale(0.001f, 0.001f, 0.001f, definitive=true)
        sceneElements[FINGER_BASE].translate(-0.5f,-0.5f,0f, definitive=true)

        //hand
        sceneElements.add(PlakObject( Utils.readSTL(this, "hand.stl"), YELLOW))
        sceneElements[HAND].scale(0.001f, 0.001f, 0.001f, definitive=true)
        sceneElements[HAND].translate(-0.5f,-0.5f,0f, definitive=true)

        //full hand
        //sceneElements.add(PlakObject( Utils.readSTL(this, "full_hand.stl"), RED))
        //scales.add(1f)
        //sceneElements[FULL_HAND].scale(0.001f, 0.001f, 0.001f, definitive=true)

        //ground
        //sceneElements.add(PlakObject( Forms.Plane(2f,2f), GREEN))
        //scales.add(1f)
        //sceneElements[GROUND].translate(-1f,0f,0f, definitive=true)

        return UPDATE_SCENE_DELAY
    }

    fun updateScene(){
        info.setFunctionName("updateScene")

        //reset position

        //rotation
        /*sceneElements.forEach { e ->
            e.rotate(0f, ANGLE_STEP, 0f)
            e.translate(-0.5f, -0.5f, 0f)
        }*/

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
