package fr.stark.steauc.gl

import fr.stark.steauc.Utils
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.cos
import kotlin.math.sin



open class PlakObject(plaks:MutableList<Plak>, givenColor:Color) {

    //movements trace
    private var position = XYZ()
    private var rotation = XYZ()
    private var scale    = XYZ(1f, 1f, 1f)
            var isMoving = false

    //plaks
    protected var plakList        : MutableList<Plak> = plaks
    protected var defaultPlakList : MutableList<Plak> = MutableList(plakList.size) { i ->
        Plak(
            XYZ( plakList[i].p1.x, plakList[i].p1.y, plakList[i].p1.z ),
            XYZ( plakList[i].p2.x, plakList[i].p2.y, plakList[i].p2.z ),
            XYZ( plakList[i].p3.x, plakList[i].p3.y, plakList[i].p3.z )
        )
    }

    //buffers
    private var vertexBuffer : FloatBuffer = Utils.FloatBuffer(plakList.size * PLAK_STRIDE)
    private var normalBuffer : FloatBuffer = Utils.FloatBuffer(plakList.size * PLAK_STRIDE)
    private var colorsBuffer : FloatBuffer = Utils.FloatBuffer(plakList.size * 4*XYZ_STRIDE)

    //color
    private val color = floatArrayOf(
        givenColor.getRed(),
        givenColor.getGreen(),
        givenColor.getBlue(),
        givenColor.getAlpha()
    )






    //BUFFERS

    //init
    init {
        //init buffers
        initColorsBuffer()
        updatePositionBuffers()
    }

    //colorsBuffer
    private fun initColorsBuffer() {
        colorsBuffer.position(0)

        //fill buffers
        for(p in plakList){
            colorsBuffer.put( color[0] ); colorsBuffer.put( color[1] ); colorsBuffer.put( color[2] ); colorsBuffer.put( color[3] )
            colorsBuffer.put( color[0] ); colorsBuffer.put( color[1] ); colorsBuffer.put( color[2] ); colorsBuffer.put( color[3] )
            colorsBuffer.put( color[0] ); colorsBuffer.put( color[1] ); colorsBuffer.put( color[2] ); colorsBuffer.put( color[3] )
        }
        colorsBuffer.position(0)
    }

    //update buffers that depends on plakList (required at each plakList modification)
    private fun updatePositionBuffers() {
        vertexBuffer.position(0)
        normalBuffer.position(0)

        //fill buffers
        for(p in plakList){
            val u = XYZ()
            val v = XYZ()
            var n = XYZ()

            // Get or calcul normal values
            if(p.n1.x != -1f){ // Normal value got from STL file (p.n1 same value as other)
                n = p.n1
            }
            else{              // Normal value to calculate
                u.x = p.p2.x - p.p1.x
                u.y = p.p2.y - p.p1.y
                u.z = p.p2.z - p.p1.z

                v.x = p.p3.x - p.p1.x
                v.y = p.p3.y - p.p1.y
                v.z = p.p3.z - p.p1.z

                n.x = u.y*v.z - u.z*v.y
                n.y = u.z*v.x - u.x-v.z
                n.z = u.x*v.y - u.y*v.x
            }

            //fill buffers
            p.p1.also{ point ->
                vertexBuffer.put( point.x ); normalBuffer.put( n.x )
                vertexBuffer.put( point.y ); normalBuffer.put( n.y )
                vertexBuffer.put( point.z ); normalBuffer.put( n.z )
            }
            p.p2.also{ point ->
                vertexBuffer.put( point.x ); normalBuffer.put( n.x )
                vertexBuffer.put( point.y ); normalBuffer.put( n.y )
                vertexBuffer.put( point.z ); normalBuffer.put( n.z )
            }
            p.p3.also{ point ->
                vertexBuffer.put( point.x ); normalBuffer.put( n.x )
                vertexBuffer.put( point.y ); normalBuffer.put( n.y )
                vertexBuffer.put( point.z ); normalBuffer.put( n.z )
            }
        }
        vertexBuffer.position(0)
        normalBuffer.position(0)
    }






    //GENERAL MOVEMENTS

    //translations
    fun translate(
        dx:Float, dy:Float, dz:Float,
        updateBuffers:Boolean=true, trace:Boolean=true
    ) {
        isMoving = true
        for(p in 0 until plakList.size){
            plakList[p].p1.translate(dx, dy, dz)
            plakList[p].p2.translate(dx, dy, dz)
            plakList[p].p3.translate(dx, dy, dz)
        }

        //update buffers if asked
        if(updateBuffers) {
            updatePositionBuffers()
        }
        isMoving = false

        //update position trace as well
        if(trace) {
            position.x += dx
            position.y += dy
            position.z += dz
        }
    }

    fun translate(p:XYZ, updateBuffers:Boolean=true, trace:Boolean=false)    = translate(
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
    ) = rotate(
        XYZ(),
        angleX, angleY, angleZ,
        updateBuffers=updateBuffers, trace=trace
    )
    fun rotate(
        center:XYZ,
        angleX:Float, angleY:Float, angleZ:Float,
        updateBuffers:Boolean=true, trace:Boolean=true
    ) {
        rotateX(center, angleX, trace=trace, updateBuffers=updateBuffers)
        rotateY(center, angleY, trace=trace, updateBuffers=updateBuffers)
        rotateZ(center, angleZ, trace=trace, updateBuffers=updateBuffers)
    }

    fun rotateX(center:XYZ, angleX:Float, trace:Boolean=true, updateBuffers:Boolean=true) {
        val cosX = cos(angleX)
        val sinX = sin(angleX)

        //apply rotation X
        isMoving = true
        for(p in 0 until plakList.size){
            plakList[p].p1.rotateX(center, cosX, sinX)
            plakList[p].p2.rotateX(center, cosX, sinX)
            plakList[p].p3.rotateX(center, cosX, sinX)
        }

        //update buffers if asked
        if(updateBuffers) {
            updatePositionBuffers()
        }
        isMoving = false

        //update traces as well
        if(trace) {
            position.rotateX(center, angleX)
            rotation.x += angleX
        }
    }

    fun rotateY(center:XYZ, angleY:Float, trace:Boolean=true, updateBuffers:Boolean=true) {
        val cosY = cos(angleY)
        val sinY = sin(angleY)

        //apply rotation Y
        isMoving = true
        for(p in 0 until plakList.size){
            plakList[p].p1.rotateY(center, cosY, sinY)
            plakList[p].p2.rotateY(center, cosY, sinY)
            plakList[p].p3.rotateY(center, cosY, sinY)
        }

        //update buffers if asked
        if(updateBuffers) {
            updatePositionBuffers()
        }
        isMoving = false

        //update traces as well
        if(trace) {
            position.rotateY(center, angleY)
            rotation.y += angleY
        }
    }

    fun rotateZ(center:XYZ, angleZ:Float, trace:Boolean=true, updateBuffers:Boolean=true) {
        val cosZ = cos(angleZ)
        val sinZ = sin(angleZ)

        //apply rotation Z
        isMoving = true
        for(p in 0 until plakList.size) {
            plakList[p].p1.rotateZ(center, cosZ, sinZ)
            plakList[p].p2.rotateZ(center, cosZ, sinZ)
            plakList[p].p3.rotateZ(center, cosZ, sinZ)
        }

        //update buffers if asked
        if(updateBuffers) {
            updatePositionBuffers()
        }
        isMoving = false

        //update traces as well
        if(trace) {
            position.rotateZ(center, angleZ)
            rotation.z += angleZ
        }
    }

    fun rotate(p:XYZ, updateBuffers:Boolean=true, trace:Boolean=false)        = rotate(
        p.x, p.y, p.z,
        updateBuffers=updateBuffers, trace=trace
    )
    fun rotateX(angleX:Float, updateBuffers:Boolean=true, trace:Boolean=true) = rotateX(
        XYZ(), angleX,
        updateBuffers=updateBuffers, trace=trace
    )
    fun rotateY(angleY:Float, updateBuffers:Boolean=true, trace:Boolean=true) = rotateY(
        XYZ(), angleY,
        updateBuffers=updateBuffers, trace=trace
    )
    fun rotateZ(angleZ:Float, updateBuffers:Boolean=true, trace:Boolean=true) = rotateZ(
        XYZ(), angleZ,
        updateBuffers=updateBuffers, trace=trace
    )



    //scale
    fun scale(sx:Float, sy:Float, sz:Float, updateBuffers:Boolean=true, trace:Boolean=true) {
        isMoving = true
        for(p in 0 until plakList.size){
            plakList[p].p1.scale(sx, sy, sz)
            plakList[p].p2.scale(sx, sy, sz)
            plakList[p].p3.scale(sx, sy, sz)
        }

        //update buffers if asked
        if(updateBuffers) {
            updatePositionBuffers()
        }
        isMoving = false

        //update scale trace as well
        if(trace) {
            scale.x *= sx
            scale.y *= sy
            scale.z *= sz
        }
    }

    fun scale(p:XYZ, updateBuffers:Boolean=true, trace:Boolean=false)        = scale(
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






    //DEFINITIVE MOVEMENTS

    //translate
    fun translateDefinitive(dx:Float, dy:Float, dz:Float) {
        for(p in 0 until defaultPlakList.size){
            defaultPlakList[p].p1.translate(dx, dy, dz)
            defaultPlakList[p].p2.translate(dx, dy, dz)
            defaultPlakList[p].p3.translate(dx, dy, dz)
        }
        updatePositionBuffers()

        //also translate normally (without keeping trace)
        translate(dx, dy, dz, trace=false)
    }

    //rotate
    fun rotateDefinitive(angleX:Float, angleY:Float, angleZ:Float) {
        val cosX   = cos(angleX)
        val sinX   = sin(angleX)
        val cosY   = cos(angleY)
        val sinY   = sin(angleY)
        val cosZ   = cos(angleZ)
        val sinZ   = sin(angleZ)
        val center = XYZ()

        //apply rotations
        for(p in 0 until defaultPlakList.size) {
            defaultPlakList[p].p1.rotateX(center, cosX, sinX)
            defaultPlakList[p].p2.rotateX(center, cosX, sinX)
            defaultPlakList[p].p3.rotateX(center, cosX, sinX)

            defaultPlakList[p].p1.rotateY(center, cosY, sinY)
            defaultPlakList[p].p2.rotateY(center, cosY, sinY)
            defaultPlakList[p].p3.rotateY(center, cosY, sinY)

            defaultPlakList[p].p1.rotateZ(center, cosZ, sinZ)
            defaultPlakList[p].p2.rotateZ(center, cosZ, sinZ)
            defaultPlakList[p].p3.rotateZ(center, cosZ, sinZ)
        }
        updatePositionBuffers()

        //also translate normally (without keeping trace)
        rotate(angleX, angleY, angleZ, trace=false)
    }

    //scale
    fun scaleDefinitive(sx:Float, sy:Float, sz:Float) {
        for(p in 0 until defaultPlakList.size){
            defaultPlakList[p].p1.scale(sx, sy, sz)
            defaultPlakList[p].p2.scale(sx, sy, sz)
            defaultPlakList[p].p3.scale(sx, sy, sz)
        }
        updatePositionBuffers()

        //also translate normally (without keeping trace)
        scale(sx, sy, sz, trace=false)
    }



    //RESET MOVEMENTS

    //reset position/rotation/scale
    fun reset(
        keepPosition:Boolean=false,
        keepRotation:Boolean=false,
        keepScale:Boolean=false,
        updateBuffers:Boolean=false
    ) {
        //reset plakList using defaultPlakList
        for(p in 0 until defaultPlakList.size){
            plakList[p].p1.x = defaultPlakList[p].p1.x
            plakList[p].p1.y = defaultPlakList[p].p1.y
            plakList[p].p1.z = defaultPlakList[p].p1.z

            plakList[p].p2.x = defaultPlakList[p].p2.x
            plakList[p].p2.y = defaultPlakList[p].p2.y
            plakList[p].p2.z = defaultPlakList[p].p2.z

            plakList[p].p3.x = defaultPlakList[p].p3.x
            plakList[p].p3.y = defaultPlakList[p].p3.y
            plakList[p].p3.z = defaultPlakList[p].p3.z
        }

        //restore position/rotation/scale
        if(keepPosition) {
            translate(position, trace=false, updateBuffers=false)
        }else{
            position = XYZ()
        }
        if(keepRotation) {
            rotate(rotation, trace=false, updateBuffers=false)
        }else{
            rotation = XYZ()
        }
        if(keepScale) {
            scale(scale, trace=false, updateBuffers=false)
        }else{
            scale = XYZ()
        }

        //update buffers if required (at the end only)
        if(updateBuffers){
            updatePositionBuffers()
        }
    }






    //UTILS

    //to string
    fun toStr() : String {
        var text = ""
        for(pl in plakList){
            text += "${pl.toStr()},\n"
        }
        return text
    }

    //getters
    fun getPosition()  = position
    fun getColor()     = color
    fun getPlaksNbr()  = plakList.size
    fun getPointsNbr() = 3 * getPlaksNbr()
    fun getCooNbr()    = 3 * getPointsNbr()

    //getters (buffers)
    fun getVertexBuffer()      = vertexBuffer
    fun getNormalBuffer()      = normalBuffer
    fun getColorsBuffer()      = colorsBuffer
    fun getVertexBuffer_size() = vertexBuffer.limit() //should be : getPointsNbr() * XYZ_STRIDE
    fun getNormalBuffer_size() = normalBuffer.limit() //should be : getPointsNbr() * XYZ_STRIDE
    fun getColorsBuffer_size() = colorsBuffer.limit() //should be : getPointsNbr() * XYZ_STRIDE
}
