package fr.stark.steauc.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import fr.stark.steauc.SceneActivity
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI






//scene refresh
const val UPDATE_SCENE_DELAY : Long = 40






class GLRenderer(givenScene:SceneActivity) : GLSurfaceView.Renderer {






    //context (for app files access)
    private val scene : SceneActivity = givenScene

    //3D objects
    private lateinit var hand : PlakObject






    //init
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        //Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        //set 3D objects
        hand = PlakObject(
            scene,
            "scene_hand.stl",
            Color(255,0,0,255)
        )
        hand.scale(10f, 10f, 10f)

        //launch timed updates
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateScene()
            }
        }, 0, UPDATE_SCENE_DELAY)
    }






    //graphic updates
    override fun onDrawFrame(unused: GL10) {

        //Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //hand
        hand.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }






    //scene update
    private fun updateScene(){

        //rotation
        hand.resetPlakList()
        //hand.rotate(0.0, PI, 0.0)
        hand.rotate(
            scene.receivedGyrX * PI/65536, // = PI/65536
            scene.receivedGyrY * PI/65536, //From [int16] to [-PI,PI]
            scene.receivedGyrZ * PI/65536
        )
        //hand.translate(0f, 0f, -0.09f)

        //debug
        //Log.i("GLRenderer >","Update !")
    }






    //layout binding
    companion object {

        fun bindRenderer(scene : SceneActivity, view: GLSurfaceView) {

            //create OpenGL ES 2.0 context
            view.setEGLContextClientVersion(2)

            //create renderer
            view.setRenderer( GLRenderer(scene) )

            //some settings
            //view.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
    }
}
