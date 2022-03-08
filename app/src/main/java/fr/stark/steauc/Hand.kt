package fr.stark.steauc

import android.content.Context
import fr.stark.steauc.gl.*
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message



//global shift
private const val GLOBAL_SHIFTX = 0.7f

//fingers
const val FINGER_THUMB  = 0
const val FINGER_INDEX  = 1
const val FINGER_MIDDLE = 2
const val FINGER_RING   = 3
const val FINGER_LITTLE = 4

//postures
const val POSTURE_NORMAL = 0
const val POSTURE_CLOSED = 1



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
    private var scale    = XYZ(1f, 1f, 1f)

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private var init_pos = XYZ()

    //finger base positions (from palm)
    var thumb_bPos  = XYZ(-10f, 45f, 13f)
    var index_bPos  = XYZ(28f, 145.5f, 13f)
    var middle_bPos = XYZ(56.2f, 145.5f, 13f)
    var ring_bPos   = XYZ(82f, 145.5f, 13f)
    var little_bPos = XYZ(107f, 145.5f, 13f)

    //phalanx sizes
    var pha_baseSize = 39.5f
    var pha_midSize  = 32.5f

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
        palm        = PlakObject( Utils.readSTL(ctx, "palm.stl"), MAGENTA)

        //thumb
        thumb_base  = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        thumb_end   = PlakObject( Utils.readSTL(ctx, "finger_end.stl"), MAGENTA)

        //index finger
        index_base  = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        index_mid   = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), MAGENTA)
        index_end   = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), MAGENTA) //end.stl"), MAGENTA)

        //middle finger
        middle_base = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        middle_mid  = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), MAGENTA)
        middle_end  = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), MAGENTA) //end.stl"), MAGENTA)

        //ring finger
        ring_base   = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        ring_mid    = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), MAGENTA)
        ring_end    = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), MAGENTA) //end.stl"), MAGENTA)

        //little finger
        little_base = PlakObject( Utils.readSTL(ctx, "finger_base.stl"), MAGENTA)
        little_mid  = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), MAGENTA)
        little_end  = PlakObject( Utils.readSTL(ctx, "finger_mid.stl"), MAGENTA) //end.stl"), MAGENTA)



        //APPLY POSITION, ROTATION & SCALE

        //set position
        init_pos = XYZ(px+GLOBAL_SHIFTX, py, pz)
        position.x = px + GLOBAL_SHIFTX
        position.y = py
        position.z = pz

        //finger rotation
        val finger_rx = (rx - Math.PI/2f).toFloat()

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
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_thumb_end",
            thumb_end,
            px=px+thumb_bPos.x, py=py+thumb_bPos.y+pha_baseSize, pz=pz+thumb_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //index finger
        scene.addElement(
            name+"_index_base",
            index_base,
            px=px+index_bPos.x, py=py+index_bPos.y, pz=pz+index_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_index_mid",
            index_mid,
            px=px+index_bPos.x, py=py+index_bPos.y+pha_baseSize, pz=pz+index_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_index_end",
            index_end,
            px=px+index_bPos.x, py=py+index_bPos.y+pha_baseSize+pha_midSize, pz=pz+index_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //middle finger
        scene.addElement(
            name+"_middle_base",
            middle_base,
            px=px+middle_bPos.x, py=py+middle_bPos.y, pz=pz+middle_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_middle_mid",
            middle_mid,
            px=px+middle_bPos.x, py=py+middle_bPos.y+pha_baseSize, pz=pz+middle_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_middle_end",
            middle_end,
            px=px+middle_bPos.x, py=py+middle_bPos.y+pha_baseSize+pha_midSize, pz=pz+middle_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //ring finger
        scene.addElement(
            name+"_ring_base",
            ring_base,
            px=px+ring_bPos.x, py=py+ring_bPos.y, pz=pz+ring_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_ring_mid",
            ring_mid,
            px=px+ring_bPos.x, py=py+ring_bPos.y+pha_baseSize, pz=pz+ring_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_ring_end",
            ring_end,
            px=px+ring_bPos.x, py=py+ring_bPos.y+pha_baseSize+pha_midSize, pz=pz+ring_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )

        //little finger
        scene.addElement(
            name+"_little_base",
            little_base,
            px=px+little_bPos.x, py=py+little_bPos.y, pz=pz+little_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_little_mid",
            little_mid,
            px=px+little_bPos.x, py=py+little_bPos.y+pha_baseSize, pz=pz+little_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
            sx=sx, sy=sy, sz=sz
        )
        scene.addElement(
            name+"_little_end",
            little_end,
            px=px+little_bPos.x, py=py+little_bPos.y+pha_baseSize+pha_midSize, pz=pz+little_bPos.z,
            rx=finger_rx, ry=ry, rz=rz,
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
    fun translate(
        dx:Float, dy:Float, dz:Float,
        updateBuffers:Boolean=true, trace:Boolean=true
    ) {
        palm.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)

        thumb_base.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        thumb_end.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)

        index_base.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        index_mid.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        index_end.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)

        middle_base.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        middle_mid.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        middle_end.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)

        ring_base.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        ring_mid.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        ring_end.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)

        little_base.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        little_mid.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)
        little_end.translate(dx, dy, dz, updateBuffers=updateBuffers, trace=trace)

        //update position trace as well
        if(trace){
            position.x += dx
            position.y += dy
            position.z += dz
        }
    }

    fun translate(p:XYZ, updateBuffers:Boolean=true, trace:Boolean=true)     = translate(
        p.x, p.y, p.z,
        updateBuffers=updateBuffers, trace=trace
    )
    fun translateX(dx:Float, updateBuffers:Boolean=true, trace:Boolean=true) = translate(
        dx, 0f, 0f,
        updateBuffers=updateBuffers, trace=trace
    )
    fun translateY(dy:Float, updateBuffers:Boolean=true, trace:Boolean=true) = translate(
        0f, dy, 0f,
        updateBuffers=updateBuffers, trace=trace
    )
    fun translateZ(dz:Float, updateBuffers:Boolean=true, trace:Boolean=true) = translate(
        0f, 0f, dz,
        updateBuffers=updateBuffers, trace=trace
    )



    //rotations
    fun rotate(
        angleX:Float, angleY:Float, angleZ:Float,
        updateBuffers:Boolean=true, trace:Boolean=true
    ) {
        rotateX(angleX, updateBuffers=updateBuffers, trace=trace)
        rotateY(angleY, updateBuffers=updateBuffers, trace=trace)
        rotateZ(angleZ, updateBuffers=updateBuffers, trace=trace)
    }

    fun rotateX(angleX:Float, updateBuffers:Boolean=true, trace:Boolean=true) {
        palm.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)

        thumb_base.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        thumb_end.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)

        index_base.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        index_mid.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        index_end.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)

        middle_base.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        middle_mid.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        middle_end.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)

        ring_base.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        ring_mid.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        ring_end.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)

        little_base.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        little_mid.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)
        little_end.rotateX(position, angleX, updateBuffers=updateBuffers, trace=trace)

        //update rotation trace as well
        if(trace){
            rotation.x += angleX
        }
    }

    fun rotateY(angleY:Float, updateBuffers:Boolean=true, trace:Boolean=true) {
        palm.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)

        thumb_base.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        thumb_end.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)

        index_base.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        index_mid.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        index_end.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)

        middle_base.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        middle_mid.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        middle_end.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)

        ring_base.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        ring_mid.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        ring_end.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)

        little_base.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        little_mid.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)
        little_end.rotateY(position, angleY, updateBuffers=updateBuffers, trace=trace)

        //update rotation trace as well
        if(trace){
            rotation.y += angleY
        }
    }

    fun rotateZ(angleZ:Float, updateBuffers:Boolean=true, trace:Boolean=true) {
        palm.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)

        thumb_base.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        thumb_end.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)

        index_base.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        index_mid.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        index_end.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)

        middle_base.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        middle_mid.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        middle_end.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)

        ring_base.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        ring_mid.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        ring_end.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)

        little_base.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        little_mid.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)
        little_end.rotateZ(position, angleZ, updateBuffers=updateBuffers, trace=trace)

        //update rotation trace as well
        if(trace){
            rotation.z += angleZ
        }
    }

    fun rotate(p:XYZ, updateBuffers:Boolean=true, trace:Boolean=true) = rotate(
        p.x, p.y, p.z,
        updateBuffers=updateBuffers, trace=trace
    )



    //scale
    fun scale(
        sx:Float, sy:Float, sz:Float,
        updateBuffers:Boolean=true, trace:Boolean=true
    ) {
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
        palm.reset(keepRotation=true, keepScale=true, updateBuffers=false)

        thumb_base.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        thumb_end.reset(keepRotation=true, keepScale=true, updateBuffers=false)

        index_base.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        index_mid.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        index_end.reset(keepRotation=true, keepScale=true, updateBuffers=false)

        middle_base.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        middle_mid.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        middle_end.reset(keepRotation=true, keepScale=true, updateBuffers=false)

        ring_base.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        ring_mid.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        ring_end.reset(keepRotation=true, keepScale=true, updateBuffers=false)

        little_base.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        little_mid.reset(keepRotation=true, keepScale=true, updateBuffers=false)
        little_end.reset(keepRotation=true, keepScale=true, updateBuffers=false)

        //place them back using updated values (updating buffers by the way)
        var bPos : XYZ
        var mPos : XYZ
        var ePos : XYZ
        palm.translate(position, updateBuffers=updateBuffers, trace=trace)

        bPos = position + thumb_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        thumb_base.translate(bPos, updateBuffers=updateBuffers, trace=trace)
        thumb_end.translate(mPos, updateBuffers=updateBuffers, trace=trace)

        bPos = position + index_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        ePos = mPos     + XYZ(0f, pha_midSize, 0f)
        index_base.translate(bPos, updateBuffers=updateBuffers, trace=trace)
        index_mid.translate(mPos, updateBuffers=updateBuffers, trace=trace)
        index_end.translate(ePos, updateBuffers=updateBuffers, trace=trace)

        bPos = position + middle_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        ePos = mPos     + XYZ(0f, pha_midSize, 0f)
        middle_base.translate(bPos, updateBuffers=updateBuffers, trace=trace)
        middle_mid.translate(mPos, updateBuffers=updateBuffers, trace=trace)
        middle_end.translate(ePos, updateBuffers=updateBuffers, trace=trace)

        bPos = position + ring_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        ePos = mPos     + XYZ(0f, pha_midSize, 0f)
        ring_base.translate(bPos, updateBuffers=updateBuffers, trace=trace)
        ring_mid.translate(mPos, updateBuffers=updateBuffers, trace=trace)
        ring_end.translate(ePos, updateBuffers=updateBuffers, trace=trace)

        bPos = position + little_bPos
        mPos = bPos     + XYZ(0f, pha_baseSize, 0f)
        ePos = mPos     + XYZ(0f, pha_midSize, 0f)
        little_base.translate(bPos, updateBuffers=updateBuffers, trace=trace)
        little_mid.translate(mPos, updateBuffers=updateBuffers, trace=trace)
        little_end.translate(ePos, updateBuffers=updateBuffers, trace=trace)

        //update scale trace as well
        if(trace){
            scale.x *= sx
            scale.y *= sy
            scale.z *= sz
        }
    }

    fun scale(p:XYZ, updateBuffers:Boolean=true, trace:Boolean=true)         = scale(
        p.x, p.y, p.z,
        updateBuffers=updateBuffers, trace=trace
    )
    fun scaleX(scaleX:Float, updateBuffers:Boolean=true, trace:Boolean=true) = scale(
        scaleX, 1f, 1f,
        updateBuffers=updateBuffers, trace=trace
    )
    fun scaleY(scaleY:Float, updateBuffers:Boolean=true, trace:Boolean=true) = scale(
        1f, scaleY, 1f,
        updateBuffers=updateBuffers, trace=trace
    )
    fun scaleZ(scaleZ:Float, updateBuffers:Boolean=true, trace:Boolean=true) = scale(
        1f, 1f, scaleZ,
        updateBuffers=updateBuffers, trace=trace
    )






    //RESET

    //trace
    fun resetTrace() {
        position = XYZ(init_pos.x, init_pos.y, init_pos.z)
        rotation = XYZ()
        scale    = XYZ(1f, 1f, 1f)
    }






    //POSTURES

    //finger classical postures
    fun setFingerPosture(finger:Int, posture:Int) {
        when(posture){
            POSTURE_NORMAL -> {
                when(finger){
                    FINGER_THUMB -> {
                        thumb_base.rotateZ(position, 0.5f)
                        thumb_end.rotateZ(position, 0.5f)
                        thumb_base.translate(0.2f, 0.4f, 0f)
                        thumb_end.translate(0.2f, 0.4f, 0f)
                    }
                    FINGER_INDEX -> {
                        index_end.rotateX(position, -0.42f)
                        index_end.translate(0f, 0.3f, 0.74f)
                        index_mid.rotateX(position, -0.3f)
                        index_mid.translate(0f, 0.22f, 0.5f)
                    }
                    FINGER_MIDDLE -> {
                        middle_end.rotateX(position, -0.42f)
                        middle_end.translate(0f, 0.3f, 0.74f)
                        middle_mid.rotateX(position, -0.3f)
                        middle_mid.translate(0f, 0.22f, 0.5f)
                    }
                    FINGER_RING -> {
                        ring_end.rotateX(position, -0.42f)
                        ring_end.translate(0f, 0.3f, 0.74f)
                        ring_mid.rotateX(position, -0.3f)
                        ring_mid.translate(0f, 0.22f, 0.5f)
                    }
                    FINGER_LITTLE -> {
                        little_end.rotateX(position, -0.42f)
                        little_end.translate(0f, 0.3f, 0.74f)
                        little_mid.rotateX(position, -0.3f)
                        little_mid.translate(0f, 0.22f, 0.5f)
                    }
                }
            }

            POSTURE_CLOSED -> {
                when(finger){
                    FINGER_THUMB -> {
                        thumb_end.rotate(position, 2.5f, -3.8f, 0f)
                        thumb_end.translate(-2f, 0.6f, -0.1f)
                        thumb_base.rotate(position, 2.5f, -3.2f, 0f)
                        thumb_base.translate(-1.65f, 0.6f, 0.25f)
                    }
                    FINGER_INDEX -> {
                        index_end.rotateX(position, 2.5f)
                        index_end.translate(0f, 0.8f, -3.2f)
                        index_mid.rotateX(position, -2.7f)
                        index_mid.translate(0f, 2.4f, -1.65f)
                        index_base.rotateX(position, -1.75f)
                        index_base.translate(0f, 2f, 0f)
                    }
                    FINGER_MIDDLE -> {
                        middle_end.rotateX(position, 2.5f)
                        middle_end.translate(0f, 0.8f, -3.2f)
                        middle_mid.rotateX(position, -2.7f)
                        middle_mid.translate(0f, 2.4f, -1.65f)
                        middle_base.rotateX(position, -1.75f)
                        middle_base.translate(0f, 2f, 0f)
                    }
                    FINGER_RING -> {
                        ring_end.rotateX(position, 2.5f)
                        ring_end.translate(0f, 0.8f, -3.2f)
                        ring_mid.rotateX(position, -2.7f)
                        ring_mid.translate(0f, 2.4f, -1.65f)
                        ring_base.rotateX(position, -1.75f)
                        ring_base.translate(0f, 2f, 0f)
                    }
                    FINGER_LITTLE -> {
                        little_end.rotateX(position, 2.5f)
                        little_end.translate(0f, 0.8f, -3.2f)
                        little_mid.rotateX(position, -2.7f)
                        little_mid.translate(0f, 2.4f, -1.65f)
                        little_base.rotateX(position, -1.75f)
                        little_base.translate(0f, 2f, 0f)
                    }
                }
            }

            /*
            POSTURE_CLOSED -> {
                when(finger){
                    FINGER_THUMB -> {
                        thumb_end.rotate(-1.2f, 0.1f, 0f)
                        thumb_end.translate(0.17f, 3.7f, -3.9f)
                        thumb_base.rotate(-1.2f, 0.1f, 0f)
                        thumb_base.translate(0.17f, 3.7f, -3.9f)
                    }
                    FINGER_INDEX -> {
                        index_end.rotateX(position, 2.5f)
                        index_end.translate(0f, 2.2f, -2.4f)
                        index_mid.rotateX(position, -2.7f)
                        index_mid.translate(0f, 3f, -0.35f)
                        index_base.rotateX(position, -1.75f)
                        index_base.translate(0f, 2f, 1f)
                    }
                    FINGER_MIDDLE -> {
                        middle_end.rotateX(position, 2.5f)
                        middle_end.translate(0f, 2.2f, -2.4f)
                        middle_mid.rotateX(position, -2.7f)
                        middle_mid.translate(0f, 3f, -0.35f)
                        middle_base.rotateX(position, -1.75f)
                        middle_base.translate(0f, 2f, 1f)
                    }
                    FINGER_RING -> {
                        ring_end.rotateX(position, 2.5f)
                        ring_end.translate(0f, 2.2f, -2.4f)
                        ring_mid.rotateX(position, -2.7f)
                        ring_mid.translate(0f, 3f, -0.35f)
                        ring_base.rotateX(position, -1.75f)
                        ring_base.translate(0f, 2f, 1f)
                    }
                    FINGER_LITTLE -> {
                        little_end.rotateX(position, 2.5f)
                        little_end.translate(0f, 2.2f, -2.4f)
                        little_mid.rotateX(position, -2.7f)
                        little_mid.translate(0f, 3f, -0.35f)
                        little_base.rotateX(position, -1.75f)
                        little_base.translate(0f, 2f, 1f)
                    }
                }
            }*/
        }
    }
}
