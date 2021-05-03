package fr.stark.steauc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import fr.stark.steauc.databinding.LyoSceneBinding
import fr.stark.steauc.log.CodeInfo



class SceneActivity : AppCompatActivity() {



    //debug info
    private val info : CodeInfo = CodeInfo("Scene", "SceneActivity.kt")

    //binding
    private lateinit var binding : LyoSceneBinding



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info.setFunctionName("onCreate")

        //init binding instance
        binding = LyoSceneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get device info
        val deviceInfo = intent.getStringExtra("Scene")

        //display device info
        findViewById<TextView>(R.id.ble_device_name).text = deviceInfo
    }

    //
}