package fr.stark.steauc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.ble.BLEScanActivity
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.databinding.LyoMenuBinding



class MenuActivity : AppCompatActivity() {

    //debug info
    private val info : CodeInfo = CodeInfo("Main", "MenuActivity.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //binding
    private lateinit var binding : LyoMenuBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msg.function("onCreate")



        // LAYOUT

        //init binding instance
        binding = LyoMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // BUTTONS

        //bind menu start

        binding.menuStartButton.setOnClickListener{
            val intent = Intent(this, BLEScanActivity::class.java)
            startActivity(intent)
        }
        /* TEMPORARILY DISABLED FOR DATA PROCESSING DEV
        binding.menuStartButton.setOnClickListener{
            val intent = Intent(this, SceneActivity::class.java)
            startActivity(intent)
        } */

        //bind menu settings
        binding.menuSettingsButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
