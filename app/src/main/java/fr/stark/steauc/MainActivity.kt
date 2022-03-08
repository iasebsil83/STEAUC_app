package fr.stark.steauc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.databinding.LyoMainBinding
import java.util.*


//delay before passing to next activity
const val MAIN_ACTIVITY_DELAY : Long = 3500 //in ms



class MainActivity : AppCompatActivity() {

    //debug info
    private val info : CodeInfo = CodeInfo("Main", "MainActivity.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //binding
    private lateinit var binding : LyoMainBinding

    //next activity
    private lateinit var next_activity : Intent



    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msg.function("onCreate")


        // LAYOUT

        //init binding instance
        binding = LyoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //AUTO-MOVE TO NEXT ACTIVITY

        //next activity intent
        next_activity = Intent(this, MenuActivity::class.java)

        //start activity after a while
        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(next_activity)
            }
        }, MAIN_ACTIVITY_DELAY)
    }
}
