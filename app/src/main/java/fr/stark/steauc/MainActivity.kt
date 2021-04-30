package fr.stark.steauc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import fr.stark.steauc.ble.BLEScanActivity
import fr.stark.steauc.databinding.LyoMainBinding
import fr.stark.steauc.log.CodeInfo


class MainActivity : AppCompatActivity() {



    //binding
    private lateinit var binding : LyoMainBinding

    //debug info
    private val info : CodeInfo = CodeInfo("Main", "MainActivity.kt")



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info.setFunctionName("onCreate")



        // LAYOUT

        //init binding instance
        binding = LyoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // BUTTONS

        //bind menu button
        binding.mainStartButton.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}