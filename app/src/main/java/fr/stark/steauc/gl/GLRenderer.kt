package fr.stark.steauc.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import fr.stark.steauc.SceneActivity
//import fr.stark.steauc.gl.forms.Cuboid
//import fr.stark.steauc.gl.forms.Plane
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI




//scene refresh
const val UPDATE_SCENE_DELAY : Long = 40 //in ms

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




class GLRenderer(givenScene:SceneActivity) : GLSurfaceView.Renderer {

    //context (for app files access)
    private val scene : SceneActivity = givenScene





    //init
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        //Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        //load 3D objects
        scene.initScene()

        //launch timed updates
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                scene.updateScene()
            }
        }, 0, UPDATE_SCENE_DELAY)
    }




    //graphic updates
    override fun onDrawFrame(unused: GL10) {

        //background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //get elements from scene
        val elements = scene.getSceneElements()

        //for each element
        for(e in 0 until elements.size) {

            //GLSL PROGRAM

            //use program
            val prog = elements[e].getProgram()
            GLES20.glUseProgram(prog)

            //get handle to vertex shader's vPosition member
            val glPosition = GLES20.glGetAttribLocation(prog, "vPosition")
            GLES20.glEnableVertexAttribArray(glPosition)

            //prepare plak coordinates
            GLES20.glVertexAttribPointer(
                glPosition,
                3,
                GLES20.GL_FLOAT,
                false,
                XYZ_STRIDE,
                elements[e].getXYZBuffer() //elements[e].getXYZBuffer()
            )

            //set fragment color
            val fragmentColor = GLES20.glGetUniformLocation(prog, "vColor")
            GLES20.glUniform4fv(fragmentColor, 1, elements[e].getColor(), 0)

            //draw
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, elements[e].getXYZCount())
            GLES20.glDisableVertexAttribArray(glPosition)
            }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }




    //GL tools
    companion object {

       //layout binding
        fun bindRenderer(scene : SceneActivity, view: GLSurfaceView) {

            //create OpenGL ES 2.0 context
            view.setEGLContextClientVersion(2)

            //create renderer
            view.setRenderer( GLRenderer(scene) )

            //some settings
            //view.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }

        //GLSL program
        fun initProgram() : Int {

            //vertex shader
            val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            GLES20.glShaderSource(vertexShader, vertexShaderCode)
            GLES20.glCompileShader(vertexShader)

            //fragment shader
            val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
            GLES20.glCompileShader(fragmentShader)

            //program
            val prog = GLES20.glCreateProgram()
            GLES20.glAttachShader(prog, vertexShader)
            GLES20.glAttachShader(prog, fragmentShader)
            GLES20.glLinkProgram(prog)

            return prog
        }
    }
}