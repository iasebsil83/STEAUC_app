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
    private var scale    = XYZ()
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
        updateBuffers()
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
    private fun updateBuffers() {
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

    //reset
    fun resetPosition(alsoBuffers:Boolean=false) {

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

        //updateBuffers() can be skipped if other actions will be performed on plakList
        if(alsoBuffers) {
            updateBuffers()
        }
    }

    //translations
    fun translate(dx:Float, dy:Float, dz:Float) {
        isMoving = true
        for(p in 0 until plakList.size){
            plakList[p].p1.translate(dx, dy, dz)
            plakList[p].p2.translate(dx, dy, dz)
            plakList[p].p3.translate(dx, dy, dz)
        }
        updateBuffers()
        isMoving = false

        //update position trace as well
        position.x += dx
        position.y += dy
        position.z += dz
    }

    fun translateX(dx:Float) = translate(dx, 0f, 0f)
    fun translateY(dy:Float) = translate(0f, dy, 0f)
    fun translateZ(dz:Float) = translate(0f, 0f, dz)



    //rotations
    fun rotate(angleX:Float, angleY:Float, angleZ:Float) = rotate( XYZ(), angleX, angleY, angleZ )
    fun rotate(center:XYZ, angleX:Float, angleY:Float, angleZ:Float) {
        rotateX(center, angleX)
        rotateY(center, angleY)
        rotateZ(center, angleZ)
    }

    fun rotateX(angleX:Float) = rotateX( XYZ(), angleX )
    fun rotateX(center:XYZ, angleX:Float) {
        val cosX = cos(angleX)
        val sinX = sin(angleX)

        //apply rotation X
        isMoving = true
        for(p in 0 until plakList.size){
            plakList[p].p1.rotateX(center, cosX, sinX)
            plakList[p].p2.rotateX(center, cosX, sinX)
            plakList[p].p3.rotateX(center, cosX, sinX)
        }
        updateBuffers()
        isMoving = false

        //update rotation trace as well
        rotation.x += angleX
    }

    fun rotateY(angleY:Float) = rotateY( XYZ(), angleY )
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

    fun rotateZ(angleZ:Float) = rotateZ( XYZ(), angleZ )
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






    //UTILS

    //print
    fun print() : String {
        var text = ""
        for(pl in plakList){
            text += "${pl.print()},\n"
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
