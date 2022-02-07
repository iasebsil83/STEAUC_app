package fr.stark.steauc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.databinding.LyoMainBinding



class MainActivity : AppCompatActivity() {

    //debug info
    private val info : CodeInfo = CodeInfo("Main", "MainActivity.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //binding
    private lateinit var binding : LyoMainBinding



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info.setFunctionName("onCreate")


        // LAYOUT

        //init binding instance
        binding = LyoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //BUTTONS

        //bind menu button
        binding.mainStartButton.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}
