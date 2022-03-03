package fr.stark.steauc

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.*
import androidx.recyclerview.widget.LinearLayoutManager
import fr.stark.steauc.ble.BLEService
import fr.stark.steauc.ble.BLEServiceAdapter
import fr.stark.steauc.databinding.LyoSceneBinding
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.gl.*
import java.lang.Math.abs


//scene refresh
const val UPDATE_SCENE_DELAY : Long = 100 //in ms

//actions
val ACTIONS = Ename(mapOf(
    "TRANSLATE" to 0,
    "ROTATE"    to 1
))

//steps
const val TRANS_STEP  = 0.02f
const val ANGLE_STEP  = 0.09f
const val SCALE_STEP  = 1.05f




class SceneActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

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

    //OpenGL renderer
    private lateinit var scene : GLRenderer

    //actions
    private var action   = 0

    //hand
    private lateinit var steauc : Hand

    //tactil
    lateinit var gestureDetector: GestureDetector
    var oldX:Float = 0.0f
    var newX:Float = 0.0f
    var oldY:Float = 0.0f
    var newY:Float = 0.0f



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msg.function("onCreate")

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

        //set OpenGL context
        scene = GLRenderer(this)
        binding.openglScene.setEGLContextClientVersion(EGL_VERSION)
        binding.openglScene.setRenderer(scene)
        setContentView(binding.root)



        //3D SCENE

        //init hand
        steauc = Hand("STEAUC")

        //init default texts
        binding.sceneSelect.text = "N/A"
        binding.sceneAction.text = ACTIONS[action]



        //EVENTS

        //bind buttons
        bindButtonEvents()


        // Tactil
        gestureDetector = GestureDetector(this, this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)

        when(event?.action){

            // When we start to swipe
            ACTION_DOWN->{
                oldX = event.x
                oldY = event.y
                newX = event.x
                newY = event.y
            }

            // When we end the swipe
            ACTION_MOVE->{
                oldX = newX
                oldY = newY
                newX = event.x
                newY = event.y

                val speed = 0.2f
                var horizonSwipe = 0.0f
                var verticalSwipe = 0.0f
                if(abs(oldX-newX) > 20){
                    horizonSwipe = if ((oldX-newX) > 0.0f) speed else -speed
                }
                if(abs(oldY-newY) > 20){
                    verticalSwipe = if (-(oldY-newY) > 0.0f) -speed else speed
                }
                steauc.rotate(verticalSwipe, horizonSwipe, 0.0f)
            }
        }

        return super.onTouchEvent(event)
    }

//GRAPHICAL SCENE

    //init
    fun initScene() : Long {
        msg.function("initScene")

        //cube
        scene.addElement(
            "Cube",
            PlakObject( Forms.Cube(0.1f), CYAN ),
            py=0.1f
        )

        //hand
        steauc.addElements(
            this, scene,
            px=-0.5f, py=-0.5f, pz=-5f,
            sx=0.01f, sy=0.01f, sz=0.01f
        )

        //set cam point of view
        //scene.zoomCam(0.001f, 0.001f, 0.001f)
        //scene.translateCam(-0.5f, -0.5f, 0f)

        return UPDATE_SCENE_DELAY
    }

    //loop
    fun updateScene(){
        msg.function("updateScene")

        //debug
        //msg.log("Update !")
    }

    fun logState() {
        msg.log("Pos${scene.getCamPos().toStr()}, Rot${scene.getCamRot().toStr()}, Sca${scene.getCamSca().toStr()}.")
    }






    //EVENTS

    //bind buttons
    fun bindButtonEvents() {

        //target element & action
        binding.sceneAction.setOnClickListener {
            action++
            if(action >= ACTIONS.length){
                action = 0
            }
            binding.sceneAction.text = ACTIONS[action]
        }

        //execute movement
        binding.sceneLeft.setOnClickListener{
            when(action) {
                ACTIONS["TRANSLATE"] -> steauc.translateX(-TRANS_STEP)
                ACTIONS["ROTATE"]    -> steauc.rotateY(-ANGLE_STEP)
            }
        }
        binding.sceneRight.setOnClickListener{
            when(action) {
                ACTIONS["TRANSLATE"] -> steauc.translateX(TRANS_STEP)
                ACTIONS["ROTATE"]    -> steauc.rotateY(ANGLE_STEP)
            }
        }
        binding.sceneDown.setOnClickListener{
            when(action) {
                ACTIONS["TRANSLATE"] -> steauc.translateY(-TRANS_STEP)
                ACTIONS["ROTATE"]    -> steauc.rotateX(-ANGLE_STEP)
            }
        }
        binding.sceneUp.setOnClickListener{
            when(action) {
                ACTIONS["TRANSLATE"] -> steauc.translateY(TRANS_STEP)
                ACTIONS["ROTATE"]    -> steauc.rotateX(ANGLE_STEP)
            }
        }
    }






    //BLE PANEL

    //link all handlers with the adapter
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int){
            msg.function("onConnectionStateChange")

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
            msg.function("onCharacteristicRead")

            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }

        override fun onCharacteristicWrite(gatt:BluetoothGatt?, characteristic:BluetoothGattCharacteristic?, status:Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            msg.function("onCharacteristicWrite")

            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }
        override fun onCharacteristicChanged(gatt:BluetoothGatt, characteristic:BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            msg.function("onCharacteristicChanged")

            runOnUiThread {
                binding.bleServicesRecView.adapter?.notifyDataSetChanged()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            msg.function("onServicesDiscovered")

            runOnUiThread {
                binding.bleServicesRecView.adapter = BLEServiceAdapter(
                    gatt,
                    gatt?.services?.map { service ->
                        BLEService(
                            service.uuid.toString(),
                            service.characteristics
                        )
                    }?.toMutableList() ?: arrayListOf(), this@SceneActivity
                )
                binding.bleServicesRecView.layoutManager = LinearLayoutManager(this@SceneActivity)
            }
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }
}
