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
const val UPDATE_SCENE_DELAY : Long = 800 //in ms

//actions
val ACTIONS = Ename(mapOf(
    "TRANSLATE" to 0,
    "ROTATE"    to 1
))

//touch event
const val TOUCH_CEIL  = 10f //in px
const val TOUCH_SPEED = 0.4f

//steps
const val ANGLE_STEP  = 0.09f




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

    //hand
    private lateinit var steauc : Hand

    //button modes
    private var thumb_closed  = false
    private var index_closed  = false
    private var middle_closed = false
    private var ring_closed   = false
    private var little_closed = false

    //touch event
    private lateinit var gestureDetector: GestureDetector
    private var oldX = 0f
    private var newX = 0f
    private var oldY = 0f
    private var newY = 0f



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



        //EVENTS

        //button events
        bindButtons()

        //touch events
        gestureDetector = GestureDetector(this, this)
    }






    //GRAPHICAL SCENE

    //init
    fun initScene() : Long {
        msg.function("initScene")

        //hand
        steauc.addElements(
            this, scene,
            px=-0.5f, py=-0.5f, pz=-5f,
            sx=0.01f, sy=0.01f, sz=0.01f
        )

        //set hand position
        steauc.translateY(-1f, updateBuffers=false)
        steauc.rotateX(-8.8f*ANGLE_STEP)

        //adjust posture
        steauc.setFingerPosture(FINGER_THUMB,  POSTURE_NORMAL)
        steauc.setFingerPosture(FINGER_INDEX,  POSTURE_NORMAL)
        steauc.setFingerPosture(FINGER_MIDDLE, POSTURE_NORMAL)
        steauc.setFingerPosture(FINGER_RING,   POSTURE_NORMAL)
        steauc.setFingerPosture(FINGER_LITTLE, POSTURE_NORMAL)


        return UPDATE_SCENE_DELAY
    }

    //loop
    fun updateScene(){
    }






    //EVENTS

    //buttons
    private fun bindButtons() {

        //finger buttons
        binding.sceneButtonThumb.setOnClickListener {
            thumb_closed = !thumb_closed
            updateHandState()
        }
        binding.sceneButtonIndex.setOnClickListener {
            index_closed = !index_closed
            updateHandState()
        }
        binding.sceneButtonMiddle.setOnClickListener {
            middle_closed = !middle_closed
            updateHandState()
        }
        binding.sceneButtonRing.setOnClickListener {
            ring_closed = !ring_closed
            updateHandState()
        }
        binding.sceneButtonLittle.setOnClickListener {
            little_closed = !little_closed
            updateHandState()
        }
    }

    private fun updateHandState() {

        //reset 3D space
        scene.resetAll(updateBuffers=false)
        steauc.resetTrace()

        //set hand position
        steauc.translateY(-1f, updateBuffers=false)
        steauc.rotateX(-4*ANGLE_STEP, updateBuffers=false)

        //update posture
        steauc.setFingerPosture(FINGER_THUMB,  if(thumb_closed)  POSTURE_CLOSED else POSTURE_NORMAL)
        steauc.setFingerPosture(FINGER_INDEX,  if(index_closed)  POSTURE_CLOSED else POSTURE_NORMAL)
        steauc.setFingerPosture(FINGER_MIDDLE, if(middle_closed) POSTURE_CLOSED else POSTURE_NORMAL)
        steauc.setFingerPosture(FINGER_RING,   if(ring_closed)   POSTURE_CLOSED else POSTURE_NORMAL)
        steauc.setFingerPosture(FINGER_LITTLE, if(little_closed) POSTURE_CLOSED else POSTURE_NORMAL)
    }

    //touch events
    override fun onTouchEvent(event: MotionEvent?): Boolean {
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

                var horizonSwipe  = 0f
                var verticalSwipe = 0f
                if(kotlin.math.abs(oldX - newX) > TOUCH_CEIL){
                    horizonSwipe = if(oldX > newX) TOUCH_SPEED else -TOUCH_SPEED
                }
                if(kotlin.math.abs(oldY - newY) > TOUCH_CEIL){
                    verticalSwipe = if(newY > oldY) -TOUCH_SPEED else TOUCH_SPEED
                }
                steauc.rotate(verticalSwipe, horizonSwipe, 0f)
            }
        }

        return false
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
}
