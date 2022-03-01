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

    //finger base positions (from palm)
    var thumb_bPos  = XYZ(-30f, -10f, 0f)
    var index_bPos  = XYZ(-30f, 20f, 0f)
    var middle_bPos = XYZ(-10f, 20f, 0f)
    var ring_bPos   = XYZ(10f, 20f, 0f)
    var little_bPos = XYZ(30f, 20f, 0f)

    //phalanx sizes
    var pha_baseSize = 15f
    var pha_midSize  = 10f

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
    fun addElements(
        ctx:Context, scene:GLRenderer,
        px:Float=0f, py:Float=0f, pz:Float=0f,
        rx:Float=0f, ry:Float=0f, rz:Float=0f,
        sx:Float=1f, sy:Float=1f, sz:Float=1f
    ) {
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



        //APPLY POSITION & SCALE

        //set position
        position.x = px
        position.y = py
        position.z = pz

        //gather scales into a XYZ
        val scales = XYZ(sx, sy, sz)

        //finger base positions scaling
        thumb_bPos  *= scales
        index_bPos  *= scales
        middle_bPos *= scales
        ring_bPos   *= scales
        little_bPos *= scales

        //phalanx sizes scaling (their concerned axis only)
        pha_baseSize *= sy
        pha_midSize  *= sy



        //ADD TO SCENE

        //palm
        scene.addElement(
            name+"_palm",
            palm,
            px=px, py=py, pz=pz,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //thumb
        scene.addElement(
            name+"_thumb_base",
            thumb_base,
            px=px+thumb_bPos.x, py=py+thumb_bPos.y, pz=pz+thumb_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_thumb_end",
            thumb_end,
            px=px+thumb_bPos.x, py=py+thumb_bPos.y+pha_baseSize, pz=pz+thumb_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //index finger
        scene.addElement(
            name+"_index_base",
            index_base,
            px=px+index_bPos.x, py=py+index_bPos.y, pz=pz+index_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_index_mid",
            index_mid,
            px=px+index_bPos.x, py=py+index_bPos.y+pha_baseSize, pz=pz+index_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_index_end",
            index_end,
            px=px+index_bPos.x, py=py+index_bPos.y+pha_baseSize+pha_midSize, pz=pz+index_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //middle finger
        scene.addElement(
            name+"_middle_base",
            middle_base,
            px=px+middle_bPos.x, py=py+middle_bPos.y, pz=pz+middle_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_middle_mid",
            middle_mid,
            px=px+middle_bPos.x, py=py+middle_bPos.y+pha_baseSize, pz=pz+middle_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_middle_end",
            middle_end,
            px=px+middle_bPos.x, py=py+middle_bPos.y+pha_baseSize+pha_midSize, pz=pz+middle_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //ring finger
        scene.addElement(
            name+"_ring_base",
            ring_base,
            px=px+ring_bPos.x, py=py+ring_bPos.y, pz=pz+ring_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_ring_mid",
            ring_mid,
            px=px+ring_bPos.x, py=py+ring_bPos.y+pha_baseSize, pz=pz+ring_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_ring_end",
            ring_end,
            px=px+ring_bPos.x, py=py+ring_bPos.y+pha_baseSize+pha_midSize, pz=pz+ring_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //little finger
        scene.addElement(
            name+"_little_base",
            little_base,
            px=px+little_bPos.x, py=py+little_bPos.y, pz=pz+little_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_little_mid",
            little_mid,
            px=px+little_bPos.x, py=py+little_bPos.y+pha_baseSize, pz=pz+little_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_little_end",
            little_end,
            px=px+little_bPos.x, py=py+little_bPos.y+pha_baseSize+pha_midSize, pz=pz+little_bPos.z,
            rx=rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
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

    fun translate(p:XYZ)     = translate(p.x, p.y, p.z)
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
        palm.rotateX(position, angleX)

        thumb_base.rotateX(position, angleX)
        thumb_end.rotateX(position, angleX)

        index_base.rotateX(position, angleX)
        index_mid.rotateX(position, angleX)
        index_end.rotateX(position, angleX)

        middle_base.rotateX(position, angleX)
        middle_mid.rotateX(position, angleX)
        middle_end.rotateX(position, angleX)

        ring_base.rotateX(position, angleX)
        ring_mid.rotateX(position, angleX)
        ring_end.rotateX(position, angleX)

        little_base.rotateX(position, angleX)
        little_mid.rotateX(position, angleX)
        little_end.rotateX(position, angleX)

        //update rotation trace as well
        rotation.x += angleX
    }

    fun rotateY(angleY:Float) {
        palm.rotateY(position, angleY)

        thumb_base.rotateY(position, angleY)
        thumb_end.rotateY(position, angleY)

        index_base.rotateY(position, angleY)
        index_mid.rotateY(position, angleY)
        index_end.rotateY(position, angleY)

        middle_base.rotateY(position, angleY)
        middle_mid.rotateY(position, angleY)
        middle_end.rotateY(position, angleY)

        ring_base.rotateY(position, angleY)
        ring_mid.rotateY(position, angleY)
        ring_end.rotateY(position, angleY)

        little_base.rotateY(position, angleY)
        little_mid.rotateY(position, angleY)
        little_end.rotateY(position, angleY)

        //update rotation trace as well
        rotation.y += angleY
    }

    fun rotateZ(angleZ:Float) {
        palm.rotateZ(position, angleZ)

        thumb_base.rotateZ(position, angleZ)
        thumb_end.rotateZ(position, angleZ)

        index_base.rotateZ(position, angleZ)
        index_mid.rotateZ(position, angleZ)
        index_end.rotateZ(position, angleZ)

        middle_base.rotateZ(position, angleZ)
        middle_mid.rotateZ(position, angleZ)
        middle_end.rotateZ(position, angleZ)

        ring_base.rotateZ(position, angleZ)
        ring_mid.rotateZ(position, angleZ)
        ring_end.rotateZ(position, angleZ)

        little_base.rotateZ(position, angleZ)
        little_mid.rotateZ(position, angleZ)
        little_end.rotateZ(position, angleZ)

        //update rotation trace as well
        rotation.z += angleZ
    }

    fun rotate(p:XYZ) = rotate(p.x, p.y, p.z)



    //scale
    fun scale(sx:Float, sy:Float, sz:Float) {
        val scales = XYZ(sx, sy, sz)

        //finger base positions scaling
        thumb_bPos  *= scales
        index_bPos  *= scales
        middle_bPos *= scales
        ring_bPos   *= scales
        little_bPos *= scales

        //phalanx sizes scaling (their concerned axis only)
        pha_baseSize *= sy
        pha_midSize  *= sy

        //reset positions (without touching buffers for now)
        palm.reset(updateBuffers=false)

        thumb_base.reset(updateBuffers=false)
        thumb_end.reset(updateBuffers=false)

        index_base.reset(updateBuffers=false)
        index_mid.reset(updateBuffers=false)
        index_end.reset(updateBuffers=false)

        middle_base.reset(updateBuffers=false)
        middle_mid.reset(updateBuffers=false)
        middle_end.reset(updateBuffers=false)

        ring_base.reset(updateBuffers=false)
        ring_mid.reset(updateBuffers=false)
        ring_end.reset(updateBuffers=false)

        little_base.reset(updateBuffers=false)
        little_mid.reset(updateBuffers=false)
        little_end.reset(updateBuffers=false)

        //place them back using updated values (updating buffers by the way)
        var bPos : XYZ
        var mPos : XYZ
        var ePos : XYZ
        palm.translate(position)

        bPos = position + thumb_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        thumb_base.translate(bPos)
        thumb_end.translate(mPos)

        bPos = position + index_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        ePos = mPos     + XYZ(0f, pha_midSize, 0f)
        index_base.translate(bPos)
        index_mid.translate(mPos)
        index_end.translate(ePos)

        bPos = position + middle_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        ePos = mPos     + XYZ(0f, pha_midSize, 0f)
        middle_base.translate(bPos)
        middle_mid.translate(mPos)
        middle_end.translate(ePos)

        bPos = position + ring_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        ePos = mPos     + XYZ(0f, pha_midSize, 0f)
        ring_base.translate(bPos)
        ring_mid.translate(mPos)
        ring_end.translate(ePos)

        bPos = position + little_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        ePos = mPos     + XYZ(0f, pha_midSize, 0f)
        little_base.translate(bPos)
        little_mid.translate(mPos)
        little_end.translate(ePos)

        //update scale trace as well
        scale.x *= sx
        scale.y *= sy
        scale.z *= sz
    }

    fun scale(p:XYZ)         = scale(p.x, p.y, p.z)
    fun scaleX(scaleX:Float) = scale(scaleX, 1f, 1f)
    fun scaleY(scaleY:Float) = scale(1f, scaleY, 1f)
    fun scaleZ(scaleZ:Float) = scale(1f, 1f, scaleZ)
}
