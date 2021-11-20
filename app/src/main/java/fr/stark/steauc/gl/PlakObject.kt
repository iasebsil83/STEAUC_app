package fr.stark.steauc.gl

import android.content.Context
import java.lang.NumberFormatException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin



open class PlakObject {

    //plaks
    protected var plakList        : MutableList<Plak>
    protected var defaultPlakList : MutableList<Plak>

    //coordinates buffer
    private var xyzBuffer : FloatBuffer
    private val xyzCount  : Int

    //color
    private val color : FloatArray

    //GLSL program
    private val program : Int = GLRenderer.initProgram()




    // INITIALIZATION

    //init using STL file
    constructor(givenContext : Context, path : String, givenColor: Color){

        //read file by blocks of 3 lines
        plakList = mutableListOf()
        var lineParity = 1
        var line1 = ""
        var line2 = ""
        Utils.readFile(givenContext, path){ line ->

            //get only vertices
            if(line.startsWith("vertex ")){
                if(lineParity == 1){

                    //store 1st line
                    line1 = line
                    lineParity = 2

                }else if(lineParity == 2){

                    //store 2nd line
                    line2 = line
                    lineParity = 3

                }else{

                    //add plak
                    val sl1 = line1.split(" ")
                    val sl2 = line2.split(" ")
                    val sl3 = line.split(" ")
                    try{
                        plakList.add(
                            Plak(
                                XYZ( sl1[1].toFloat(), sl1[2].toFloat(), sl1[3].toFloat() ),
                                XYZ( sl2[1].toFloat(), sl2[2].toFloat(), sl2[3].toFloat() ),
                                XYZ( sl3[1].toFloat(), sl3[2].toFloat(), sl3[3].toFloat() )
                            )
                        )
                    }catch(e : NumberFormatException){
                    }

                    lineParity = 1
                }
            }
        }

        //save default values
        defaultPlakList = mutableListOf()
        for(pl in plakList){
            defaultPlakList.add(
                Plak(
                    XYZ( pl.p1.getX(), pl.p1.getY(), pl.p1.getZ() ),
                    XYZ( pl.p2.getX(), pl.p2.getY(), pl.p2.getZ() ),
                    XYZ( pl.p3.getX(), pl.p3.getY(), pl.p3.getZ() )
                )
            )
        }

        //set coordinates
        xyzCount  = plakList.size * 3
        xyzBuffer = newXYZBuffer(plakList)

        //set color
        color = floatArrayOf(
            givenColor.getRed(),
            givenColor.getGreen(),
            givenColor.getBlue(),
            givenColor.getAlpha()
        )
    }

    //init using plakList
    constructor(plaks : MutableList<Plak>, givenColor : Color){
        plakList = plaks

        //save default values
        defaultPlakList = mutableListOf()
        for(pl in plakList){
            defaultPlakList.add(
                Plak(
                    XYZ( pl.p1.getX(), pl.p1.getY(), pl.p1.getZ() ),
                    XYZ( pl.p2.getX(), pl.p2.getY(), pl.p2.getZ() ),
                    XYZ( pl.p3.getX(), pl.p3.getY(), pl.p3.getZ() )
                )
            )
        }

        //set coordinates
        xyzCount = plaks.size * 3
        xyzBuffer = newXYZBuffer(plaks)

        //set color
        color = floatArrayOf(
            givenColor.getRed(),
            givenColor.getGreen(),
            givenColor.getBlue(),
            givenColor.getAlpha()
        )
    }




    // BUFFERS

    //XYZBuffer
    private fun newXYZBuffer(plaks:MutableList<Plak>) : FloatBuffer {

        //allocate vertex buffer
        val byteBuf = ByteBuffer.allocateDirect(plaks.size * PLAK_STRIDE)
        byteBuf.order(ByteOrder.nativeOrder())

        //set xyzCount et xyzBuffer
        val floatBuf = byteBuf.asFloatBuffer()

        //fill vertexBuffer
        for(p in plaks){
            p.p1.also{ point ->
                floatBuf.put( point.getX() )
                floatBuf.put( point.getY() )
                floatBuf.put( point.getZ() )
            }
            p.p2.also{ point ->
                floatBuf.put( point.getX() )
                floatBuf.put( point.getY() )
                floatBuf.put( point.getZ() )
            }
            p.p3.also{ point ->
                floatBuf.put( point.getX() )
                floatBuf.put( point.getY() )
                floatBuf.put( point.getZ() )
            }
        }
        floatBuf.position(0)

        return floatBuf
    }

    private fun resetXYZBuffer() {
        resetPosition()
        xyzBuffer = newXYZBuffer(plakList)
    }




    //GENERAL MOVEMENTS

    //position
    fun resetPosition(){

        //reset plakList from defaultPlakList
        for(p in 0 until defaultPlakList.size){
            plakList[p] = Plak(
                XYZ( defaultPlakList[p].p1.getX(), defaultPlakList[p].p1.getY(), defaultPlakList[p].p1.getZ() ),
                XYZ( defaultPlakList[p].p2.getX(), defaultPlakList[p].p2.getY(), defaultPlakList[p].p2.getZ() ),
                XYZ( defaultPlakList[p].p3.getX(), defaultPlakList[p].p3.getY(), defaultPlakList[p].p3.getZ() )
            )
        }
    }

    fun translate(dx:Float, dy:Float, dz:Float, definitive:Boolean = false){
        if(definitive) {
            PL_translate(defaultPlakList, dx, dy, dz)
        }
        PL_translate(plakList, dx, dy, dz)

        //update buffer
        xyzBuffer = newXYZBuffer(plakList)
    }

    //rotations
    fun rotate(angleX:Double, angleY:Double, angleZ:Double, definitive:Boolean = false) = rotate(
        XYZ(), angleX, angleY, angleZ,
        definitive=definitive
    )

    fun rotate(center:XYZ, angleX:Double, angleY:Double, angleZ:Double, definitive:Boolean = false){
        rotateX(center, angleX, definitive=definitive)
        rotateY(center, angleY, definitive=definitive)
        rotateZ(center, angleZ, definitive=definitive)
    }

    fun rotateX(angleX:Double, definitive:Boolean = false) = rotateX(
        XYZ(), angleX,
        definitive=definitive
    )
    fun rotateX(center:XYZ, angleX:Double, definitive:Boolean = false){
        val cosX = cos(angleX)
        val sinX = sin(angleX)

        //apply rotation X
        if(definitive) {
            PL_rotateX(defaultPlakList, center, cosX, sinX)
        }
        PL_rotateX(plakList, center, cosX, sinX)

        //update buffer
        xyzBuffer = newXYZBuffer(plakList)
    }

    fun rotateY(angleY:Double, definitive:Boolean = false) = rotateY(
        XYZ(), angleY,
        definitive=definitive
    )
    fun rotateY(center:XYZ, angleY:Double, definitive: Boolean = false){
        val cosY = cos(angleY)
        val sinY = sin(angleY)

        //apply rotation Y
        if(definitive) {
            PL_rotateY(defaultPlakList, center, cosY, sinY)
        }
        PL_rotateY(plakList, center, cosY, sinY)

        //update buffer
        xyzBuffer = newXYZBuffer(plakList)
    }

    fun rotateZ(angleZ:Double, definitive:Boolean = false) = rotateZ(
        XYZ(), angleZ,
        definitive=definitive
    )
    fun rotateZ(center:XYZ, angleZ:Double, definitive: Boolean = false){
        val cosZ = cos(angleZ)
        val sinZ = sin(angleZ)

        //apply rotation Z
        if(definitive) {
            PL_rotateZ(defaultPlakList, center, cosZ, sinZ)
        }
        PL_rotateZ(plakList, center, cosZ, sinZ)

        //update buffer
        xyzBuffer = newXYZBuffer(plakList)
    }

    //scale
    fun scale(sx:Float, sy:Float, sz:Float, definitive:Boolean = false){

        //apply scale
        if(definitive) {
            PL_scale(defaultPlakList, sx, sy, sz)
        }
        PL_scale(plakList, sx, sy, sz)

        //update buffer
        xyzBuffer = newXYZBuffer(plakList)
    }




    //PlakList movements
    private fun PL_translate(pl:MutableList<Plak>, dx:Float, dy:Float, dz:Float){
        for(p in 0 until pl.size){
            pl[p].p1.translate(dx, dy, dz)
            pl[p].p2.translate(dx, dy, dz)
            pl[p].p3.translate(dx, dy, dz)
        }
    }

    private fun PL_rotateX(pl:MutableList<Plak>, center:XYZ, cosX:Double, sinX:Double){
        for(p in 0 until pl.size){
            pl[p].p1.rotateX(center, cosX, sinX)
            pl[p].p2.rotateX(center, cosX, sinX)
            pl[p].p3.rotateX(center, cosX, sinX)
        }
    }

    private fun PL_rotateY(pl:MutableList<Plak>, center:XYZ, cosY:Double, sinY:Double){
        for(p in 0 until pl.size){
            pl[p].p1.rotateY(center, cosY, sinY)
            pl[p].p2.rotateY(center, cosY, sinY)
            pl[p].p3.rotateY(center, cosY, sinY)
        }
    }

    private fun PL_rotateZ(pl:MutableList<Plak>, center:XYZ, cosZ:Double, sinZ:Double){
        for(p in 0 until pl.size){
            pl[p].p1.rotateZ(center, cosZ, sinZ)
            pl[p].p2.rotateZ(center, cosZ, sinZ)
            pl[p].p3.rotateZ(center, cosZ, sinZ)
        }
    }

    private fun PL_scale(pl:MutableList<Plak>, scaleX:Float, scaleY:Float, scaleZ:Float){
        for(p in 0 until pl.size){
            pl[p].p1.scale(scaleX, scaleY, scaleZ)
            pl[p].p2.scale(scaleX, scaleY, scaleZ)
            pl[p].p3.scale(scaleX, scaleY, scaleZ)
        }
    }




    //getters
    fun getColor()     = color
    fun getXYZBuffer() = xyzBuffer
    fun getXYZCount()  = xyzCount
    fun getProgram()   = program
}