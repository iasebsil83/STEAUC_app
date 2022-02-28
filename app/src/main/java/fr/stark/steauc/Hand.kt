package fr.stark.steauc

import android.content.Context
import fr.stark.steauc.Utils
import fr.stark.steauc.gl.*
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import kotlin.math.cos
import kotlin.math.sin



class Hand(givenName:String) {

    //debug info
    private val info : CodeInfo = CodeInfo("Hand", "Hand.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //name
    private val name = givenName

    //movements trace
    private var position = XYZ()
    private var rotation = XYZ()
    private var scale    = XYZ()

    //finger positions
    var THUMB_BASE_POSITION  = XYZ()
    var INDEX_BASE_POSITION  = XYZ()
    var MIDDLE_BASE_POSITION = XYZ()
    var RING_BASE_POSITION   = XYZ()
    var LITTLE_BASE_POSITION = XYZ()
    var FINGER_BASE_SIZE = 1f
    var FINGER_MID_SIZE  = 1f

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






    // GENERAL MOVEMENTS

    //translations
    fun translate(dx:Float, dy:Float, dz:Float) {
        palm.translate(dx, dy, dz)

        thumb_base.translate(dx, dy, dz)
        thumb_end.translate(dx, dy, dz)

        index_base.translate(dx, dy, dz)
        index_mid.translate(dx, dy, dz)
        index_end.translate(dx, dy, dz)

        middle_base.translate(dx, dy, dz)
        middle_mid.translate(dx, dy, dz)
        middle_end.translate(dx, dy, dz)

        ring_base.translate(dx, dy, dz)
        ring_mid.translate(dx, dy, dz)
        ring_end.translate(dx, dy, dz)

        little_base.translate(dx, dy, dz)
        little_mid.translate(dx, dy, dz)
        little_end.translate(dx, dy, dz)

        //update position trace as well
        position.x += dx
        position.y += dy
        position.z += dz
    }

    fun translateX(dx:Float) = translate(dx, 0f, 0f)
    fun translateY(dy:Float) = translate(0f, dy, 0f)
    fun translateZ(dz:Float) = translate(0f, 0f, dz)



    //rotations
    fun rotate(angleX:Float, angleY:Float, angleZ:Float) {
        rotateX(angleX)
        rotateY(angleY)
        rotateZ(angleZ)
    }

    fun rotateX(angleX:Float) {
        val cosX = cos(angleX)
        val sinX = sin(angleX)

        //apply rotation X
        palm.rotateX(position, cosX, sinX)

        index_base.rotateX(position, cosX, sinX)
        index_mid.rotateX(position, cosX, sinX)
        index_end.rotateX(position, cosX, sinX)

        //update rotation trace as well
        rotation.x += angleX
    }

    fun rotateY(center:XYZ, angleY:Float) {
        val cosY = cos(angleY)
        val sinY = sin(angleY)

        //apply rotation Y
        isMoving = true
        for(p in 0 until plakList.size){
            plakList[p].p1.rotateY(center, cosY, sinY)
            plakList[p].p2.rotateY(center, cosY, sinY)
            plakList[p].p3.rotateY(center, cosY, sinY)
        }
        updateBuffers()
        isMoving = false

        //update rotation trace as well
        rotation.y += angleY
    }

    fun rotateZ(center:XYZ, angleZ:Float) {
        val cosZ = cos(angleZ)
        val sinZ = sin(angleZ)

        //apply rotation Z
        isMoving = true
        for(p in 0 until plakList.size) {
            plakList[p].p1.rotateZ(center, cosZ, sinZ)
            plakList[p].p2.rotateZ(center, cosZ, sinZ)
            plakList[p].p3.rotateZ(center, cosZ, sinZ)
        }
        updateBuffers()
        isMoving = false

        //update rotation trace as well
        rotation.z += angleZ
    }



    //scale
    fun scale(scaleX:Float, scaleY:Float, scaleZ:Float) {

        //apply scale
        isMoving = true
        for(p in 0 until plakList.size){
            plakList[p].p1.scale(scaleX, scaleY, scaleZ)
            plakList[p].p2.scale(scaleX, scaleY, scaleZ)
            plakList[p].p3.scale(scaleX, scaleY, scaleZ)
        }
        updateBuffers()
        isMoving = false

        //update scale trace as well
        scale.x += scaleX
        scale.y += scaleY
        scale.z += scaleZ
    }

    fun scaleX(scaleX:Float) = scale(scaleX, 1f, 1f)
    fun scaleY(scaleY:Float) = scale(1f, scaleY, 1f)
    fun scaleZ(scaleZ:Float) = scale(1f, 1f, scaleZ)
}
