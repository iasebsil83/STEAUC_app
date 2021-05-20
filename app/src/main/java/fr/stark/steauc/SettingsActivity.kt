package fr.stark.steauc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.databinding.LyoSettingsBinding



class SettingsActivity : AppCompatActivity() {



    //binding
    private lateinit var binding : LyoSettingsBinding



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // LAYOUT

        //init binding instance
        binding = LyoSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}