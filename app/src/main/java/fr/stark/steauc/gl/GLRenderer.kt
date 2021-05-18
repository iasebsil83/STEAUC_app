package fr.stark.steauc.gl

import android.content.Context
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
import android.opengl.GLSurfaceView



class GLRenderer(givenContext : Context) : GLSurfaceView.Renderer {



    //context (for app files access)
    private val context : Context = givenContext

    //3D objects
    private lateinit var tristant : PlakObject
    private lateinit var thor     : PlakObject



    //init
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        //Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        //set 3D objects
        tristant = PlakObject(
            mutableListOf(
                Plak(
                    XYZ(0.0f, 0.622008459f, 0.0f),
                    XYZ(-0.5f, -0.311004243f, 0.0f),
                    XYZ(0.5f, -0.311004243f, 0.0f)
                )
            ),
            Color(0,255,0,255)
        )
        thor = PlakObject(
                context,
                "scene_torus.obj",
                Color(255,0,0,255)
        )
    }



    //graphic updates
    override fun onDrawFrame(unused: GL10) {

        //Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        tristant.draw()
        thor.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }



    //layout binding
    companion object {

        fun bindRenderer(context : Context, view: GLSurfaceView) {

            //create OpenGL ES 2.0 context
            view.setEGLContextClientVersion(2)

            //create renderer
            view.setRenderer( GLRenderer(context) )

            //some settings
            view.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
    }
}
