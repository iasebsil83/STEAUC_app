package fr.stark.steauc.gl

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


//memory allocation
const val FLOAT_STRIDE : Int = 4 //4 bytes
const val XYZ_STRIDE   : Int = 3 * FLOAT_STRIDE



class XYZ { // <=> vertex

    //coordinates
    var x : Float
    var y : Float
    var z : Float



    //init
    constructor(){
        x = 0f
        y = 0f
        z = 0f
    }

    constructor(givenX:Float, givenY:Float, givenZ:Float){
        x = givenX
        y = givenY
        z = givenZ
    }



    //movements
    fun translate(dx:Float, dy:Float, dz:Float) {
        x += dx
        y += dy
        z += dz
    }
    fun rotate(center:XYZ, angleX:Float, angleY:Float, angleZ:Float) {
        rotateX(center, angleX)
        rotateY(center, angleY)
        rotateZ(center, angleZ)
    }



    //axial rotations
    fun rotateX(center:XYZ, angle:Float) {
        val cosA = cos(angle)
        val sinA = sin(angle)

        rotateX(center, cosA,sinA)
    }

    fun rotateY(center:XYZ, angle:Float) {
        val cosA = cos(angle)
        val sinA = sin(angle)

        rotateY(center, cosA,sinA)
    }

    fun rotateZ(center:XYZ, angle:Float) {
        val cosA = cos(angle)
        val sinA = sin(angle)

        rotateZ(center, cosA,sinA)
    }



    //optimizated axial rotations (avoid cos & sin calculations)
    fun rotateX(center:XYZ, cosA:Float, sinA:Float) {
        val dy = y - center.y
        val dz = z - center.z

        //new coordinates
        y = center.y + (dy*cosA - dz*sinA)
        z = center.z + (dy*sinA + dz*cosA)
    }

    fun rotateY(center:XYZ, cosA:Float, sinA:Float) {
        val dx = x - center.x
        val dz = z - center.z

        //new coordinates
        x = center.x + (dz*sinA + dx*cosA)
        z = center.z + (dz*cosA - dx*sinA)
    }

    fun rotateZ(center:XYZ, cosA:Float, sinA:Float) {
        val dx = x - center.x
        val dy = y - center.y

        //new coordinates
        x = center.x + (dx*cosA - dy*sinA)
        y = center.y + (dx*sinA + dy*cosA)
    }



    //scale
    fun scale(scaleX:Float, scaleY:Float, scaleZ:Float) {
        x *= scaleX
        y *= scaleY
        z *= scaleZ
    }



    //basic operations
    operator fun plus(p:XYZ) : XYZ = XYZ(
        x + p.x,
        y + p.y,
        z + p.z
    )

    operator fun minus(p:XYZ) : XYZ = XYZ(
        x - p.x,
        y - p.y,
        z - p.z
    )

    operator fun times(p:XYZ) : XYZ = XYZ(
        x * p.x,
        y * p.y,
        z * p.z
    )

    operator fun div(p:XYZ) : XYZ = XYZ(
        x / p.x,
        y / p.y,
        z / p.z
    )



    //display
    fun print() : String {
        return "($x,$y,$z)"
    }



    //static operations
    companion object {

        //scalar
        fun scalProd(p1:XYZ, p2:XYZ) : Float = (
                p1.x*p2.x +
                p1.y*p2.y +
                p1.z*p2.z
        )

        //vectorial product
        fun vectProd(p1:XYZ, p2:XYZ) : XYZ = XYZ(
            p1.y * p2.z - p2.y * p1.z,
            p1.z * p2.x - p2.z * p1.x,
            p1.x * p2.y - p2.x * p1.y
        )

        //distance
        fun norm(p1:XYZ, p2:XYZ) : Float {
            val delta = p2 - p1
            return sqrt(scalProd(delta, delta))
        }
    }
}
