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

    //debug info
    private val info : CodeInfo = CodeInfo("GL", "gl/PlakObject.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

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
    private var colorsBuffer : FloatBuffer = Utils.FloatBuffer(plakList.size * PLAK_STRIDE)

    //color
    private val color = floatArrayOf(
        givenColor.getRed(),
        givenColor.getGreen(),
        givenColor.getBlue(),
        givenColor.getAlpha()
    )

    //GLSL program
    private val program : Int = GLRenderer.initProgram()




    //init
    init {
        updateBuffers() //fill buffers
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

    //translation
    fun translate(dx:Float, dy:Float, dz:Float, definitive:Boolean=false) {
        if(definitive) {
            for(p in 0 until defaultPlakList.size){
                defaultPlakList[p].p1.translate(dx, dy, dz)
                defaultPlakList[p].p2.translate(dx, dy, dz)
                defaultPlakList[p].p3.translate(dx, dy, dz)
            }
        }
        for(p in 0 until plakList.size){
            plakList[p].p1.translate(dx, dy, dz)
            plakList[p].p2.translate(dx, dy, dz)
            plakList[p].p3.translate(dx, dy, dz)
        }
        updateBuffers()
    }

    //rotations
    fun rotate(angleX:Float, angleY:Float, angleZ:Float, definitive:Boolean=false) = rotate(
        XYZ(), angleX, angleY, angleZ,
        definitive=definitive
    )

    fun rotate(center:XYZ, angleX:Float, angleY:Float, angleZ:Float, definitive:Boolean=false) {
        rotateX(center, angleX, definitive=definitive)
        rotateY(center, angleY, definitive=definitive)
        rotateZ(center, angleZ, definitive=definitive)
    }

    fun rotateX(angleX:Float, definitive:Boolean=false) = rotateX(
        XYZ(), angleX,
        definitive=definitive
    )
    fun rotateX(center:XYZ, angleX:Float, definitive:Boolean=false) {
        val cosX = cos(angleX)
        val sinX = sin(angleX)

        //apply rotation X
        if(definitive) {
            for(p in 0 until defaultPlakList.size){
                defaultPlakList[p].p1.rotateX(center, cosX, sinX)
                defaultPlakList[p].p2.rotateX(center, cosX, sinX)
                defaultPlakList[p].p3.rotateX(center, cosX, sinX)
            }
        }
        for(p in 0 until plakList.size){
            plakList[p].p1.rotateX(center, cosX, sinX)
            plakList[p].p2.rotateX(center, cosX, sinX)
            plakList[p].p3.rotateX(center, cosX, sinX)
        }
        updateBuffers()
    }

    fun rotateY(angleY:Float, definitive:Boolean=false) = rotateY(
        XYZ(), angleY,
        definitive=definitive
    )
    fun rotateY(center:XYZ, angleY:Float, definitive: Boolean=false) {
        val cosY = cos(angleY)
        val sinY = sin(angleY)

        //apply rotation Y
        if(definitive) {
            for(p in 0 until defaultPlakList.size){
                defaultPlakList[p].p1.rotateY(center, cosY, sinY)
                defaultPlakList[p].p2.rotateY(center, cosY, sinY)
                defaultPlakList[p].p3.rotateY(center, cosY, sinY)
            }
        }
        for(p in 0 until plakList.size){
            plakList[p].p1.rotateY(center, cosY, sinY)
            plakList[p].p2.rotateY(center, cosY, sinY)
            plakList[p].p3.rotateY(center, cosY, sinY)
        }
        updateBuffers()
    }

    fun rotateZ(angleZ:Float, definitive:Boolean=false) = rotateZ(
        XYZ(), angleZ,
        definitive=definitive
    )
    fun rotateZ(center:XYZ, angleZ:Float, definitive: Boolean=false) {
        val cosZ = cos(angleZ)
        val sinZ = sin(angleZ)

        //apply rotation Z
        if(definitive) {
            for(p in 0 until defaultPlakList.size){
                defaultPlakList[p].p1.rotateZ(center, cosZ, sinZ)
                defaultPlakList[p].p2.rotateZ(center, cosZ, sinZ)
                defaultPlakList[p].p3.rotateZ(center, cosZ, sinZ)
            }
        }
        for(p in 0 until plakList.size){
            plakList[p].p1.rotateZ(center, cosZ, sinZ)
            plakList[p].p2.rotateZ(center, cosZ, sinZ)
            plakList[p].p3.rotateZ(center, cosZ, sinZ)
        }
        updateBuffers()
    }

    //scale
    fun scale(scaleX:Float, scaleY:Float, scaleZ:Float, definitive:Boolean=false) {

        //apply scale
        if(definitive) {
            for(p in 0 until defaultPlakList.size){
                defaultPlakList[p].p1.scale(scaleX, scaleY, scaleZ)
                defaultPlakList[p].p2.scale(scaleX, scaleY, scaleZ)
                defaultPlakList[p].p3.scale(scaleX, scaleY, scaleZ)
            }
        }
        for(p in 0 until plakList.size){
            plakList[p].p1.scale(scaleX, scaleY, scaleZ)
            plakList[p].p2.scale(scaleX, scaleY, scaleZ)
            plakList[p].p3.scale(scaleX, scaleY, scaleZ)
        }
        updateBuffers()
    }




    //BUFFERS

    //update buffers that depends on plakList (required at each plakList modification)
    private fun updateBuffers() {
        vertexBuffer.position(0)
        normalBuffer.position(0)

        //fill buffers
        for(p in plakList){

            //get normal vector (vectorial product)
            val diff12 = p.p2 - p.p1
            val diff13 = p.p3 - p.p1
            var normal = XYZ.vectProd(diff12, diff13)

            //fill buffers
            p.p1.also{ point ->
                vertexBuffer.put( point.x ); normalBuffer.put( normal.x )
                vertexBuffer.put( point.y ); normalBuffer.put( normal.y )
                vertexBuffer.put( point.z ); normalBuffer.put( normal.z )
            }
            p.p2.also{ point ->
                vertexBuffer.put( point.x ); normalBuffer.put( normal.x )
                vertexBuffer.put( point.y ); normalBuffer.put( normal.y )
                vertexBuffer.put( point.z ); normalBuffer.put( normal.z )
            }
            p.p3.also{ point ->
                vertexBuffer.put( point.x ); normalBuffer.put( normal.x )
                vertexBuffer.put( point.y ); normalBuffer.put( normal.y )
                vertexBuffer.put( point.z ); normalBuffer.put( normal.z )
            }
        }
        vertexBuffer.position(0)
        normalBuffer.position(0)
    }

    fun initColorBuffer() {
        colorsBuffer.position(0)

        //fill buffer
        for(p in plakList){

            //fill buffer
            colorsBuffer.put( color[0] ); colorsBuffer.put( color[0] ); colorsBuffer.put( color[0] )
            colorsBuffer.put( color[1] ); colorsBuffer.put( color[1] ); colorsBuffer.put( color[1] )
            colorsBuffer.put( color[2] ); colorsBuffer.put( color[2] ); colorsBuffer.put( color[2] )
        }
        vertexBuffer.position(0)
    }




    //getters
    fun getColor()     = color
    fun getPlaksNbr()  = plakList.size
    fun getPointsNbr() = 3 * getPlaksNbr()
    fun getCooNbr()    = 3 * getPointsNbr()
    fun getProgram()   = program

    //getters (buffers)
    fun getVertexBuffer()      = vertexBuffer
    fun getNormalBuffer()      = normalBuffer
    fun getColorsBuffer()      = colorsBuffer
    fun getVertexBuffer_size() = vertexBuffer.limit() //should be : getPointsNbr() * XYZ_STRIDE
    fun getNormalBuffer_size() = normalBuffer.limit() //should be : getPointsNbr() * XYZ_STRIDE
    fun getColorsBuffer_size() = colorsBuffer.limit() //should be : getPointsNbr() * XYZ_STRIDE
}
