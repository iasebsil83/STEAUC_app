package fr.stark.steauc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.ble.BLEScanActivity
import fr.stark.steauc.databinding.LyoMenuBinding



class MenuActivity : AppCompatActivity() {



    //binding
    private lateinit var binding : LyoMenuBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



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

        //bind menu settings
        binding.menuSettingsButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
