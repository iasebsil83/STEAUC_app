package fr.stark.steauc

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.ble.BLEInteractActivity
import fr.stark.steauc.ble.BLEScanActivity
import fr.stark.steauc.databinding.LyoSceneBinding
import fr.stark.steauc.gl.GLRenderer
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message



class SceneActivity : AppCompatActivity() {



    //debug info
    private val info : CodeInfo = CodeInfo("Scene", "SceneActivity.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //openGL view
    private lateinit var glView   : GLSurfaceView
    private lateinit var renderer : GLRenderer

    //binding
    private lateinit var binding : LyoSceneBinding



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info.setFunctionName("onCreate")



        //CHECK RECEIVED INFO

        //get device info
        val device     = intent.getParcelableExtra<BluetoothDevice>("BLEDevice")
        val deviceName = intent.getStringExtra("BLEDeviceName")

        //go back to previous activity if incorrect info received
        if(device == null) {
            val intent = Intent(this, BLEScanActivity::class.java)
            startActivity(intent)
        }



        //LAYOUT

        //init binding instance
        binding = LyoSceneBinding.inflate(layoutInflater)



        //3D SCENE

        //init renderer
        GLRenderer.bindRenderer(this, binding.openglScene)

        //display
        setContentView(binding.root)



        //BUTTONS

        //bind ble interact button
        binding.sceneBleInteractButton.setOnClickListener{
            val intent = Intent(this, BLEInteractActivity::class.java)
            intent.putExtra("BLEDevice2", device)
            intent.putExtra("BLEDeviceName2", deviceName)
            startActivity(intent)
        }
    }
}
