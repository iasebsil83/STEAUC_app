package fr.stark.steauc

import android.content.Context
import fr.stark.steauc.Utils
import fr.stark.steauc.gl.*
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message



class Hand(givenName:String) {

    //debug info
    private val info : CodeInfo = CodeInfo("Hand", "Hand.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //name
    private val name = givenName

    //palm
    private lateinit var palm : PlakObject

    //thumb
    private lateinit var thumb_base  : PlakObject
    private lateinit var thumb_end   : PlakObject

    //index finger
    private lateinit var index_base  : PlakObject
    private lateinit var index_mid   : PlakObject
    private lateinit var index_end   : PlakObject

    //middle finger
    private lateinit var middle_base : PlakObject
    private lateinit var middle_mid  : PlakObject
    private lateinit var middle_end  : PlakObject

    //ring finger
    private lateinit var ring_base   : PlakObject
    private lateinit var ring_mid    : PlakObject
    private lateinit var ring_end    : PlakObject

    //little finger
    private lateinit var little_base : PlakObject
    private lateinit var little_mid  : PlakObject
    private lateinit var little_end  : PlakObject




    //elements
    fun addElements(ctx:Context, scene:GLRenderer) {
        msg.function("addElements")



        //LOAD STRUCTURES

        //palm
        palm        = PlakObject( Utils.readSTL(ctx, "palm.stl"), BLUE)

        //thumb
        thumb_base  = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        thumb_end   = PlakObject( Utils.readSTL(ctx, "finger_end.stl"), YELLOW)

        //index finger
        index_base  = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        index_mid   = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), RED)
        index_end   = PlakObject( Utils.readSTL(ctx, "finger_end.stl"), YELLOW)

        //middle finger
        middle_base = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        middle_mid  = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), RED)
        middle_end  = PlakObject( Utils.readSTL(ctx, "finger_end.stl"), YELLOW)

        //ring finger
        ring_base   = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        ring_mid    = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), RED)
        ring_end    = PlakObject( Utils.readSTL(ctx, "finger_end.stl"), YELLOW)

        //little finger
        little_base = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        little_mid  = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), RED)
        little_end  = PlakObject( Utils.readSTL(ctx, "finger_end.stl"), YELLOW)



        //ADD TO SCENE

        //palm
        scene.addElement(name+"_palm", palm)

        //thumb
        scene.addElement(name+"_thumb_base", thumb_base)
        scene.addElement(name+"_thumb_end",  thumb_end)

        //index finger
        scene.addElement(name+"_index_base", index_base)
        scene.addElement(name+"_index_mid",  index_mid)
        scene.addElement(name+"_index_end",  index_end)

        //middle finger
        scene.addElement(name+"_middle_base", middle_base)
        scene.addElement(name+"_middle_mid",  middle_mid)
        scene.addElement(name+"_middle_end",  middle_end)

        //ring finger
        scene.addElement(name+"_ring_base", ring_base)
        scene.addElement(name+"_ring_mid",  ring_mid)
        scene.addElement(name+"_ring_end",  ring_end)

        //little finger
        scene.addElement(name+"_little_base", little_base)
        scene.addElement(name+"_little_mid",  little_mid)
        scene.addElement(name+"_little_end",  little_end)
    }




    //getters
    fun getElementsNames() = listOf(
        name+"_palm",

        //thumb
        name+"_thumb_base",
        name+"_thumb_end",

        //index
        name+"_index_base",
        name+"_index_mid",
        name+"_index_end",

        //middle
        name+"_middle_base",
        name+"_middle_mid",
        name+"_middle_end",

        //ring
        name+"_ring_base",
        name+"_ring_mid",
        name+"_ring_end",

        //middle
        name+"_little_base",
        name+"_middle_mid",
        name+"_middle_end"
    )

    fun scale(sX:Float, sY:Float, sZ:Float) {
        //
    }
}
