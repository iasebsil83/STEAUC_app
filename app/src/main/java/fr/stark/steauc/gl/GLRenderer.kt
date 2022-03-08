package fr.stark.steauc.gl

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import fr.stark.steauc.Ename
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.SceneActivity
import fr.stark.steauc.log.CODEINFO__RUNTIME_ERROR
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10



//version
const val EGL_VERSION : Int = 3

//shaders
const val VERTEX_SHADER = """
    uniform mat4 u_MVPMatrix;
    uniform mat4 u_MVMatrix;
    uniform vec3 u_LightPos;
    attribute vec4 a_Position;
    attribute vec4 a_Color;
    attribute vec3 a_Normal;
    varying vec4 v_Color;
    void main(){
       vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
       vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
       float distance       = length(u_LightPos - modelViewVertex);
       vec3 lightVector     = normalize(u_LightPos - modelViewVertex);
       float diffuse        = 2.0 + max(dot(modelViewNormal, lightVector), 0.1);
       diffuse              = diffuse * (1.0 / (2.0 + distance));
       v_Color              = a_Color * diffuse;
       gl_Position          = u_MVPMatrix * a_Position;
   }
"""

const val FRAGMENT_SHADER = """
    precision mediump float;
    varying vec4 v_Color;
    void main(){
       gl_FragColor = v_Color;
    }
"""

const val POINT_VERTEX_SHADER = """
    uniform mat4 u_MVPMatrix;
    attribute vec4 a_Position;
    void main(){
       gl_Position = u_MVPMatrix * a_Position;
       gl_PointSize = 5.0;
    }
"""

const val POINT_FRAGMENT_SHADER = """
    precision mediump float;
    void main(){
       gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
    }
"""

//frustum
const val FRUSTUM_BOTTOM = -1f
const val FRUSTUM_TOP    =  1f
const val FRUSTUM_NEAR   =  1f
const val FRUSTUM_FAR    = 10f




class GLRenderer(givenActivity:SceneActivity) : GLSurfaceView.Renderer {

    //debug info
    private val info : CodeInfo = CodeInfo("GL", "gl/GLRenderer.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //context (for app files access)
    private val activity : SceneActivity = givenActivity

    //camera
    private val camPos = XYZ()
    private val camRot = XYZ()
    private val camSca = XYZ(1f, 1f, 1f)

    //scene elements
    private var elements   : MutableList<PlakObject> = mutableListOf()
    private var elementsID                           = Ename()

    //matrices
    private val modelMatrix      = FloatArray(16) //used to move models from object space to world space.
    private val viewMatrix       = FloatArray(16) //like camera, used to transforms world space to eye space
    private val projectionMatrix = FloatArray(16) //project scene onto 2D viewport
    private val MVPMatrix        = FloatArray(16) //final combined matrix (passed for shader program
    private val lightModelMatrix = FloatArray(16) //use to store copy of model matrix for light position

    //buffers handlers
    private var MVPMatrixHandle = 0 //used to pass in the transformation matrix
    private var MVMatrixHandle  = 0 //used to pass in the modelview matrix
    private var lightPosHandle  = 0 //used to pass in the light position
    private var positionHandle  = 0 //used to pass in model position
    private var colorHandle     = 0 //user to pass in model color
    private var normalHandle    = 0 //used to pass in model normal

    //data size
    private val POS_DATA_SIZE  = 3
    private val COL_DATA_SIZE  = 4
    private val NORM_DATA_SIZE = 3

    //light handlers
    private val lightPosInModelSpace = floatArrayOf(1f, 1f, -1f, 10f) //hold light centered on the origin in model space (4th coord needed -> multiplication with transform matrices
    private val lightPosInWorldSpace = FloatArray(4)                   //hold current pos of the light in world space (after transformation via model matrix)
    private val lightPosInEyeSpace   = FloatArray(4)                   //hold the transformed pos of the light in eye space (after transformation via modelview matrix)

    //program handlers
    private var perVertexProgramHandle = 0
    private var pointProgramHandle     = 0




    //init
    override fun onSurfaceCreated(unused:GL10, config:EGLConfig) {
        msg.function("onSurfaceCreated")

        //set the background frame color
        GLES30.glClearColor(0.3f, 0.3f, 0.3f, 1.0f)

        //launch timed updates (delay is returned from the initScene() call)
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity.updateScene()
            }
        }, 0, activity.initScene())

        //use culling to remove back faces (optimisation)
        //GLES30.glEnable(GLES30.GL_CULL_FACE)

        // Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        //set point of view
        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 0f, 0f,
            0f, 0f, -5f,
            0f, 1f, 0f
        )

        //set shaders
        perVertexProgramHandle = createAndLinkProgram(
            compileShader(GLES30.GL_VERTEX_SHADER,   VERTEX_SHADER),
            compileShader(GLES30.GL_FRAGMENT_SHADER, FRAGMENT_SHADER),
            arrayOf("a_Position", "a_Color", "a_Normal")
        )
        pointProgramHandle = createAndLinkProgram(
            compileShader(GLES30.GL_VERTEX_SHADER,   POINT_VERTEX_SHADER),
            compileShader(GLES30.GL_FRAGMENT_SHADER, POINT_FRAGMENT_SHADER),
            arrayOf("a_Position")
        )
    }




    //graphic updates
    override fun onDrawFrame(unused:GL10) {
        msg.function("onDrawFrame")

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        GLES30.glUseProgram(perVertexProgramHandle) // Draw program

        //set program handles for drawing
        MVPMatrixHandle = GLES30.glGetUniformLocation(perVertexProgramHandle, "u_MVPMatrix")
        MVMatrixHandle  = GLES30.glGetUniformLocation(perVertexProgramHandle, "u_MVMatrix")
        lightPosHandle  = GLES30.glGetUniformLocation(perVertexProgramHandle, "u_LightPos")
        positionHandle  = GLES30.glGetAttribLocation(perVertexProgramHandle, "a_Position")
        colorHandle     = GLES30.glGetAttribLocation(perVertexProgramHandle, "a_Color")
        normalHandle    = GLES30.glGetAttribLocation(perVertexProgramHandle, "a_Normal")

        //calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(lightModelMatrix, 0)
        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0)
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0)

        //draw elements
        for(e in elements) {
            if(!e.isMoving){
                drawPlakObject(e)
            }
        }

        //draw a point to indicate the light
        GLES30.glUseProgram(pointProgramHandle)
        drawLight()
    }

    override fun onSurfaceChanged(unused:GL10, width:Int, height:Int) {
        msg.function("onSurfaceChanged")

        //set the OpenGL viewport to the same size as the surface.
        GLES30.glViewport(0, 0, width, height)

        //create new perspective projection matrix
        val FRUSTRUM_RIGHT = width.toFloat()/height
        val FRUSTRUM_LEFT = -FRUSTRUM_RIGHT

        Matrix.frustumM(
            projectionMatrix, 0,
            FRUSTRUM_LEFT,  FRUSTRUM_RIGHT,
            FRUSTUM_BOTTOM, FRUSTUM_TOP,
            FRUSTUM_NEAR,   FRUSTUM_FAR
        )
    }




    // Shaders tools
    private fun compileShader(shaderType:Int, shaderSource:String) : Int {
        var shaderHandle = GLES30.glCreateShader(shaderType)
        if (shaderHandle != 0) {
            //pass in the shader source & compile it
            GLES30.glShaderSource(shaderHandle, shaderSource)
            GLES30.glCompileShader(shaderHandle)

            //get the compilation status
            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0)

            //if the compilation failed, delete the shader
            if (compileStatus[0] == 0) {
                Log.e(
                    "error",
                    "Error compiling shader: " + GLES30.glGetShaderInfoLog(shaderHandle)
                )
                GLES30.glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }
        if (shaderHandle == 0) {
            throw RuntimeException("Error creating shader.")
        }
        return shaderHandle
    }

    private fun createAndLinkProgram(vertexShaderHandle:Int, fragmentShaderHandle:Int, attributes:Array<String>?) : Int {
        var programHandle = GLES30.glCreateProgram()
        if (programHandle != 0) {

            //bind the vertex & fragment shader to the program
            GLES30.glAttachShader(programHandle, vertexShaderHandle)
            GLES30.glAttachShader(programHandle, fragmentShaderHandle)

            //bind attributes
            if (attributes != null) {
                val size = attributes.size
                for (i in 0 until size) {
                    GLES30.glBindAttribLocation(programHandle, i, attributes[i])
                }
            }

            //link the two shaders together into a program
            GLES30.glLinkProgram(programHandle)

            //get the link status
            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                err.log(CODEINFO__RUNTIME_ERROR, "Error compiling program (" + GLES30.glGetProgramInfoLog(programHandle) + ")")
                GLES30.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }
        if (programHandle == 0) {
            throw java.lang.RuntimeException("Error creating program.")
        }
        return programHandle
    }



    // Drawing
    private fun drawLight() {
        val pointMVPMatrixHandle = GLES30.glGetUniformLocation(pointProgramHandle, "u_MVPMatrix")
        val pointPositionHandle = GLES30.glGetAttribLocation(pointProgramHandle, "a_Position")

        //pass in the position
        GLES30.glVertexAttrib3f(
            pointPositionHandle,
            lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]
        )

        //since we are not using a buffer object, disable vertex arrays for this attribute
        GLES30.glDisableVertexAttribArray(pointPositionHandle)

        //pass in the transformation matrix
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, lightModelMatrix, 0)
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0)
        GLES30.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, MVPMatrix, 0)

        //draw the point
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1)
    }

    private fun drawPlakObject(po:PlakObject){
        Matrix.setIdentityM(modelMatrix, 0)

        //set vertex buffer
        po.getVertexBuffer().position(0)
        GLES30.glVertexAttribPointer(
            positionHandle,
            POS_DATA_SIZE,
            GLES30.GL_FLOAT,
            false,
            0,
            po.getVertexBuffer()
        )
        GLES30.glEnableVertexAttribArray(positionHandle)

        //set color buffer
        po.getColorsBuffer().position(0)
        GLES30.glVertexAttribPointer(
            colorHandle,
            COL_DATA_SIZE,
            GLES30.GL_FLOAT,
            false,
            0,
            po.getColorsBuffer()
        )
        GLES30.glEnableVertexAttribArray(colorHandle)

        //set normal buffer
        po.getNormalBuffer().position(0)
        GLES30.glVertexAttribPointer(
            normalHandle,
            NORM_DATA_SIZE,
            GLES30.GL_FLOAT,
            false,
            0,
            po.getNormalBuffer()
        )
        GLES30.glEnableVertexAttribArray(normalHandle)

        //get MVP matrix & pass in the modelview matrix (with MVP matrix = view matrix * model matrix)
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        GLES30.glUniformMatrix4fv(MVMatrixHandle, 1, false, MVPMatrix, 0)

        //get MVP matrix & pass in the combined matrix (with MVP matrix = modelview matrix * projection matrix)
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0)
        GLES30.glUniformMatrix4fv(MVPMatrixHandle, 1, false, MVPMatrix, 0)

        //pass in the light position in eye space
        GLES30.glUniform3f(
            lightPosHandle,
            lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]
        )

        //draw the plak
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, po.getCooNbr())
    }






    //CAMERA

    //movements
    fun translateCam(tx:Float, ty:Float, tz:Float) {
        for(e in elements){
            e.translate(tx, ty, tz, trace=false)
        }

        //keep camera trace only (scene elements are not really moving)
        camPos.x += tx
        camPos.y += ty
        camPos.z += tz
    }
    fun rotateCam(rx:Float, ry:Float, rz:Float) {
        for(e in elements){
            e.rotate(rx, ry, rz, trace=false)
        }

        //keep camera trace only (scene elements are not really moving)
        camRot.x += rx
        camRot.y += ry
        camRot.z += rz
    }
    fun zoomCam(sx:Float, sy:Float, sz:Float) {
        for(e in elements){
            e.scale(sx, sz, sz, trace=false)
        }

        //keep camera trace only (scene elements are not really moving)
        camSca.x *= sx
        camSca.y *= sy
        camSca.z *= sz
    }






    //SCENE ELEMENTS

    //elements
    fun addElement(
        name:String, po:PlakObject,
        px:Float=0f, py:Float=0f, pz:Float=0f,
        rx:Float=0f, ry:Float=0f, rz:Float=0f,
        sx:Float=1f, sy:Float=1f, sz:Float=1f
    ) {

        //name already taken
        if(name in elementsID){
            err.log(CODEINFO__RUNTIME_ERROR, "Object could not be added, name '${name}' is already taken.")
            return
        }

        //apply definitive placements
        po.scaleDefinitive(sx,sy,sz)
        po.rotateDefinitive(rx,ry,rz)
        po.translateDefinitive(px,py,pz)

        //add element
        elementsID.add(elements.size, name)
        elements.add(po)
    }

    fun addElements(
        elem:Map<String, PlakObject>,
        px:Float=0f, py:Float=0f, pz:Float=0f,
        rx:Float=0f, ry:Float=0f, rz:Float=0f,
        sx:Float=1f, sy:Float=1f, sz:Float=1f
    ) {
        for(e in elem) {
            addElement(
                e.key, e.value,
                px=px, py=py, pz=pz,
                rx=rx, ry=ry, rz=rz,
                sx=sx, sy=sy, sz=sz
            )
        }
    }

    fun resetAll(updateBuffers:Boolean=true, trace:Boolean=true) {
        for(e in elements){
            e.reset(updateBuffers=updateBuffers, trace=trace)
        }
    }




    //getters
    fun getCamPos() = camPos
    fun getCamRot() = camRot
    fun getCamSca() = camSca
    fun getElement(name:String) = elements[ elementsID[name] ]
}
