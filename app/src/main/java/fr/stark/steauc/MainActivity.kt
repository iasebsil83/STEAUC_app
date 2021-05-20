package fr.stark.steauc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.databinding.LyoMainBinding



class MainActivity : AppCompatActivity() {



    //binding
    private lateinit var binding : LyoMainBinding



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



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
