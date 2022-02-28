package fr.stark.steauc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.databinding.LyoSettingsBinding



class SettingsActivity : AppCompatActivity() {

    //debug info
    private val info : CodeInfo = CodeInfo("Main", "SettingsActivity.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //binding
    private lateinit var binding : LyoSettingsBinding



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msg.function("onCreate")



        // LAYOUT

        //init binding instance
        binding = LyoSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
