package fr.stark.steauc.gl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_VERTEX_SHADER
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer






//shaders
const val vertexShaderCode =
    "attribute vec4 vPosition;"  +
    "void main() {"              +
    "  gl_Position = vPosition;" +
    "}"
const val fragmentShaderCode =
    "precision mediump float;" +
    "uniform vec4 vColor;"     +
    "void main() {"            +
    "  gl_FragColor = vColor;" +
    "}"



//memory allocation
const val FloatStride : Int = 4 //4 bytes
const val XYZStride   : Int = 3 * FloatStride
const val PlakStride  : Int = 3 * XYZStride






class PlakObject {






    //GLSL program
    private val program   : Int

    //coordinates
    private val xyzBuffer : FloatBuffer
    private val xyzCount  : Int

    //color
    private val color     : FloatArray






    //init
    constructor(givenContext : Context, path : String, givenColor: Color){

        //read file by blocks of 3 lines
        var tempPlakList : MutableList<Plak> = mutableListOf()
        var lineParity = 1
        var line1 = ""
        var line2 = ""
        Utils.readFile(givenContext, path){ line ->

            //get only vertices
            if(line.startsWith("v ")){
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
                    tempPlakList.add(
                        Plak(
                            XYZ( sl1[1].toFloat(), sl1[2].toFloat(), sl1[3].toFloat() ),
                            XYZ( sl2[1].toFloat(), sl2[2].toFloat(), sl2[3].toFloat() ),
                            XYZ( sl3[1].toFloat(), sl3[2].toFloat(), sl3[3].toFloat() )
                        )
                    )

                    lineParity = 1

                }
            }
        }

        //set coordinates
        xyzCount = tempPlakList.size * 3
        xyzBuffer = initXYZBuffer(tempPlakList)

        //set color
        color = floatArrayOf(
            givenColor.getRed(),
            givenColor.getGreen(),
            givenColor.getBlue(),
            givenColor.getAlpha()
        )

        //set GLSL program
        program = initProgram()
    }

    constructor(plaks : MutableList<Plak>, givenColor : Color){

        //set coordinates
        xyzCount = plaks.size * 3
        xyzBuffer = initXYZBuffer(plaks)

        //set color
        color = floatArrayOf(
            givenColor.getRed(),
            givenColor.getGreen(),
            givenColor.getBlue(),
            givenColor.getAlpha()
        )

        //set GLSL program
        program = initProgram()
    }






    //init : Float buffer & GLSL program
    private fun initXYZBuffer(plaks : MutableList<Plak>) : FloatBuffer {

        //allocate vertex buffer
        val byteBuf = ByteBuffer.allocateDirect(plaks.size * PlakStride)
        byteBuf.order(ByteOrder.nativeOrder())

        //set xyzCount et xyzBuffer
        val floatBuf = byteBuf.asFloatBuffer()

        //fill vertexBuffer
        for(p in plaks){
            p.getP1().also{ point ->
                floatBuf.put( point.getX() )
                floatBuf.put( point.getY() )
                floatBuf.put( point.getZ() )
            }
            p.getP2().also{ point ->
                floatBuf.put( point.getX() )
                floatBuf.put( point.getY() )
                floatBuf.put( point.getZ() )
            }
            p.getP3().also{ point ->
                floatBuf.put( point.getX() )
                floatBuf.put( point.getY() )
                floatBuf.put( point.getZ() )
            }
        }
        floatBuf.position(0)

        return floatBuf
    }

    private fun initProgram() : Int {

        //vertex shader
        val vertexShader = GLES20.glCreateShader(GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertexShaderCode)
        GLES20.glCompileShader(vertexShader)

        //fragment shader
        val fragmentShader = GLES20.glCreateShader(GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
        GLES20.glCompileShader(fragmentShader)

        //program
        val prog = GLES20.glCreateProgram()
        GLES20.glAttachShader(prog, vertexShader)
        GLES20.glAttachShader(prog, fragmentShader)
        GLES20.glLinkProgram(prog)

        return prog
    }






    //display
    fun draw() {
        GLES20.glUseProgram(program)

        //get handle to vertex shader's vPosition member
        val GLSL_position = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(GLSL_position)

        //prepare plak coordinates
        GLES20.glVertexAttribPointer(
            GLSL_position,
            3,
            GLES20.GL_FLOAT,
            false,
            XYZStride,
            xyzBuffer
        )

        //get GLSL color
        val GLSL_color = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(GLSL_color, 1, color, 0)

        //draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, xyzCount)
        GLES20.glDisableVertexAttribArray(GLSL_position)
    }
}
