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
    private val camSca = XYZ()

    //scene elements
    private var elements   : MutableList<PlakObject> = mutableListOf()
    private var elementsID                           = Ename()

    //matrices
    private val modelMatrix      = FloatArray(16) // Used to move models from object space to world space.
    private val viewMatrix       = FloatArray(16) // Like camera, used to transforms world space to eye space
    private val projectionMatrix = FloatArray(16) // Project scene onto 2D viewport
    private val MVPMatrix        = FloatArray(16) // Final combined matrix (passed for shader program
    private val lightModelMatrix = FloatArray(16) // Use to store copy of model matrix for light position

    //buffers handlers
    private var MVPMatrixHandle = 0 // Used to pass in the transformation matrix
    private var MVMatrixHandle  = 0 // Used to pass in the modelview matrix
    private var lightPosHandle  = 0 // Used to pass in the light position
    private var positionHandle  = 0 // Used to pass in model position
    private var colorHandle     = 0 // User to pass in model color
    private var normalHandle    = 0 // Used to pass in model normal

    //data size
    private val POS_DATA_SIZE  = 3
    private val COL_DATA_SIZE  = 4
    private val NORM_DATA_SIZE = 3

    //light handlers
    private val lightPosInModelSpace = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f) // Hold light centered on the origin in model space (4th coord needed -> multiplication with transform matrices
    private val lightPosInWorldSpace = FloatArray(4)                   // Hold current pos of the light in world space (after transformation via model matrix)
    private val lightPosInEyeSpace   = FloatArray(4)                   // Hold the transformed pos of the light in eye space (after transformation via modelview matrix)

    //program handlers
    private var perVertexProgramHandle = 0
    private var pointProgramHandle = 0

    //shaders
    private val pointVertexShader = """
        uniform mat4 u_MVPMatrix;
        attribute vec4 a_Position;
        void main(){
           gl_Position = u_MVPMatrix * a_Position;
           gl_PointSize = 5.0;
        }
    """

    private val pointFragmentShader = """
        precision mediump float;
        void main(){
           gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
        }
    """




    //init
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        msg.function("onSurfaceCreated")

        //Set the background frame color
        GLES30.glClearColor(0.3f, 0.3f, 0.3f, 1.0f)

        //launch timed updates (delay is returned from the initScene() call)
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity.updateScene()
            }
        }, 0, activity.initScene())

        // Use culling to remove back faces (optimisation)
        //GLES30.glEnable(GLES30.GL_CULL_FACE)

        // Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        // Position the eye in front of the origin
        val eyeX = 0.0f
        val eyeY = 0.0f
        val eyeZ = -0.5f

        // We are looking toward the distance
        val lookX = 0.0f
        val lookY = 0.0f
        val lookZ = -5.0f

        // Set our up vector. This is where our head would be pointing were we holding the camera
        val upX = 0.0f
        val upY = 1.0f
        val upZ = 0.0f

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ)

        // Set shaders
        val vertexShader         = getVertexShader()
        val fragmentShader       = getFragmentShader()
        val vertexShaderHandle   = compileShader(GLES30.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderHandle = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader)
        perVertexProgramHandle   = createAndLinkProgram(
            vertexShaderHandle,
            fragmentShaderHandle,
            arrayOf("a_Position", "a_Color", "a_Normal")
        )
        val pointVertexShaderHandle   = compileShader(GLES30.GL_VERTEX_SHADER, pointVertexShader)
        val pointFragmentShaderHandle = compileShader(GLES30.GL_FRAGMENT_SHADER, pointFragmentShader)
        pointProgramHandle            = createAndLinkProgram(
            pointVertexShaderHandle,
            pointFragmentShaderHandle,
            arrayOf("a_Position")
        )
    }




    //graphic updates
    override fun onDrawFrame(unused: GL10) {
        msg.function("onDrawFrame")

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        // Do a complete rotation every 10 seconds.
        val time           = SystemClock.uptimeMillis() % 10000L
        val angleInDegrees = 360.0f / 10000.0f * time.toInt()

        GLES30.glUseProgram(perVertexProgramHandle) // Draw program

        // Set program handles for drawing
        MVPMatrixHandle = GLES30.glGetUniformLocation(perVertexProgramHandle, "u_MVPMatrix")
        MVMatrixHandle  = GLES30.glGetUniformLocation(perVertexProgramHandle, "u_MVMatrix")
        lightPosHandle  = GLES30.glGetUniformLocation(perVertexProgramHandle, "u_LightPos")
        positionHandle  = GLES30.glGetAttribLocation(perVertexProgramHandle, "a_Position")
        colorHandle     = GLES30.glGetAttribLocation(perVertexProgramHandle, "a_Color")
        normalHandle    = GLES30.glGetAttribLocation(perVertexProgramHandle, "a_Normal")

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(lightModelMatrix, 0)
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -2.0f)
        Matrix.rotateM(lightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
        Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0)
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0)

        //draw elements
        for(e in elements) {
            drawPlakObject(e)
        }

        // Draw a point to indicate the light.
        GLES30.glUseProgram(pointProgramHandle)
        drawLight()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        msg.function("onSurfaceChanged")

        // Set the OpenGL viewport to the same size as the surface.
        GLES30.glViewport(0, 0, width, height)

        // Create new perspective projection matrix (height will stay the same / width vary as per aspect ratio
        val ratio = width.toFloat() / height
        val left = -ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 10.0f

        Matrix.frustumM(projectionMatrix, 0, left, ratio, bottom, top, near, far)
    }




    // Shaders tools
    private fun compileShader(shaderType: Int, shaderSource: String): Int {
        var shaderHandle = GLES30.glCreateShader(shaderType)
        if (shaderHandle != 0) {
            // Pass in the shader source & compile it
            GLES30.glShaderSource(shaderHandle, shaderSource)
            GLES30.glCompileShader(shaderHandle)

            // Get the compilation status.
            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0)

            // If the compilation failed, delete the shader.
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
    private fun createAndLinkProgram(vertexShaderHandle: Int, fragmentShaderHandle: Int, attributes: Array<String>?): Int {
        var programHandle = GLES30.glCreateProgram()
        if (programHandle != 0) {
            // Bind the vertex & fragment shader to the program.
            GLES30.glAttachShader(programHandle, vertexShaderHandle)
            GLES30.glAttachShader(programHandle, fragmentShaderHandle)

            // Bind attributes
            if (attributes != null) {
                val size = attributes.size
                for (i in 0 until size) {
                    GLES30.glBindAttribLocation(programHandle, i, attributes[i])
                }
            }

            // Link the two shaders together into a program.
            GLES30.glLinkProgram(programHandle)

            // Get the link status.
            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                Log.e(
                    "error",
                    "Error compiling program: " + GLES30.glGetProgramInfoLog(programHandle)
                )
                GLES30.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }
        if (programHandle == 0) {
            throw java.lang.RuntimeException("Error creating program.")
        }
        return programHandle
    }



    // Shaders
    private fun getVertexShader(): String {
        return """
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
               float diffuse        = max(dot(modelViewNormal, lightVector), 0.1);
               diffuse              = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));
               v_Color              = a_Color * diffuse;
               gl_Position          = u_MVPMatrix * a_Position;
            }
        """
    }

    private fun getFragmentShader(): String {
        return """
            precision mediump float;
            varying vec4 v_Color;
            void main(){
               gl_FragColor = v_Color;
            }
        """
    }



    // Drawing
    private fun drawLight() {
        val pointMVPMatrixHandle = GLES30.glGetUniformLocation(pointProgramHandle, "u_MVPMatrix")
        val pointPositionHandle = GLES30.glGetAttribLocation(pointProgramHandle, "a_Position")

        // Pass in the position
        GLES30.glVertexAttrib3f(
            pointPositionHandle,
            lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]
        )

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES30.glDisableVertexAttribArray(pointPositionHandle)

        // Pass in the transformation matrix.
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, lightModelMatrix, 0)
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0)
        GLES30.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, MVPMatrix, 0)

        // Draw the point.
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1)
    }

    private fun drawPlakObject(po: PlakObject){
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

        // Get MVP matrix & pass in the modelview matrix (with MVP matrix = view matrix * model matrix)
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        GLES30.glUniformMatrix4fv(MVMatrixHandle, 1, false, MVPMatrix, 0)

        // Get MVP matrix & pass in the combined matrix (with MVP matrix = modelview matrix * projection matrix)
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0)
        GLES30.glUniformMatrix4fv(MVPMatrixHandle, 1, false, MVPMatrix, 0)

        // Pass in the light position in eye space.
        GLES30.glUniform3f(
            lightPosHandle,
            lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]
        )

        // Draw the plak
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, po.getCooNbr())
    }






    //CAMERA

    //movements
    fun translateCam(tx:Float, ty:Float, tz:Float) {
        for(e in elements){
            e.translate(tx, ty, tz)
        }
        camPos.x += tx
        camPos.y += ty
        camPos.z += tz
    }
    fun rotateCam(rx:Float, ry:Float, rz:Float) {
        for(e in elements){
            e.rotate(rx, ry, rz)
        }
        camRot.x += rx
        camRot.y += ry
        camRot.z += rz
    }
    fun zoomCam(sx:Float, sy:Float, sz:Float) {
        for(e in elements){
            e.scale(sx, sz, sz)
        }
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

        //apply placement
        po.translate(px,py,pz, definitive=true)
        po.rotate(rx,ry,rz, definitive=true)
        po.scale(sx,sy,sz, definitive=true)

        //add element
        elementsID.add(elements.size, name)
        elements.add(po)
    }

    fun addElements(
        elem:Map<String,PlakObject>,
        px:Float=0f, py:Float=0f, pz:Float=0f,
        rx:Float=0f, ry:Float=0f, rz:Float=0f,
        sx:Float=1f, sy:Float=1f, sz:Float=1f
    ) {
        for(e in elem) {
            this.addElement(
                e.key, e.value,
                px=px, py=py, pz=pz,
                rx=rx, ry=ry, rz=rz,
                sx=sx, sy=sy, sz=sz
            )
        }
    }




    //getters
    fun getCamPos() = camPos
    fun getCamRot() = camRot
    fun getCamSca() = camSca
    fun getElement(name:String) = elements[ elementsID[name] ]
}
