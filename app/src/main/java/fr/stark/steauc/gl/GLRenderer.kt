package fr.stark.steauc.gl

import android.opengl.EGL14
import android.opengl.GLES10
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.SceneActivity
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10



//version
const val EGL_VERSION : Int = 3

//buffers
const val NULL_INDEX          = 0
const val VERTEX_BUFFER_INDEX = 1
const val INDICE_BUFFER_INDEX = 2

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

    //debug info
    private val info : CodeInfo = CodeInfo("GL", "gl/GLRenderer.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //context (for app files access)
    private val scene : SceneActivity = givenScene





    //init
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        info.setFunctionName("onSurfaceCreated")

        //Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        //launch timed updates (delay is returned from the initScene() call)
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                scene.updateScene()
            }
        }, 0, scene.initScene())
    }




    //graphic updates
    override fun onDrawFrame(unused: GL10) {
        info.setFunctionName("onDrawFrame")

        //background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        //for each element in the scene
        for(e in scene.getSceneElements()) {

            //GLSL PROGRAM

            //use program
            val prog = e.getProgram()
            GLES30.glUseProgram(prog)


            //prepare plak coordinates
            GLES30.glVertexAttribPointer(
                0,
                3,
                GLES30.GL_FLOAT,
                false,
                0,
                e.getVertexBuffer()
            )

            //enable
            GLES30.glEnableVertexAttribArray(0)

            //set fragment color
            val fragmentColor = GLES30.glGetUniformLocation(prog, "vColor")
            GLES30.glUniform4fv(fragmentColor, 1, e.getColor(), 0)

            //draw
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, e.getCooNbr())
            GLES30.glDisableVertexAttribArray(0)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        info.setFunctionName("onSurfaceChanged")

        GLES30.glViewport(0, 0, width, height)
    }




    //GL tools
    companion object {

       //layout binding
        fun bindRenderer(scene : SceneActivity, view: GLSurfaceView) {

            //create OpenGL ES context
            view.setEGLContextClientVersion(EGL_VERSION)

            //create renderer
            view.setRenderer( GLRenderer(scene) )

            //some settings
            //view.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }

        //GLSL program
        fun initProgram() : Int {

            //vertex shader
            val vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER)
            GLES30.glShaderSource(vertexShader, vertexShaderCode)
            GLES30.glCompileShader(vertexShader)

            //fragment shader
            val fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER)
            GLES30.glShaderSource(fragmentShader, fragmentShaderCode)
            GLES30.glCompileShader(fragmentShader)

            //program
            val prog = GLES30.glCreateProgram()
            GLES30.glAttachShader(prog, vertexShader)
            GLES30.glAttachShader(prog, fragmentShader)

            //get handle to vertex shader's vPosition member
            GLES30.glBindAttribLocation(prog, 0, "vPosition")
            GLES30.glLinkProgram(prog)

            return prog
        }
    }
}
