package fr.stark.steauc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.databinding.LyoSettingsBinding
import fr.stark.steauc.log.CodeInfo



class SettingsActivity : AppCompatActivity() {



    //binding
    private lateinit var binding : LyoSettingsBinding

    //debug info
    private val info : CodeInfo = CodeInfo("Settings", "SettingsActivity.kt")



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info.setFunctionName("onCreate")



        // LAYOUT

        //init binding instance
        binding = LyoSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}