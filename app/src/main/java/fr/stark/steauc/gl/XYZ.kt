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
    fun translate(tx:Float, ty:Float, tz:Float) {
        x += tx
        y += ty
        z += tz
    }
    fun rotate(center:XYZ, rx:Float, ry:Float, rz:Float) {
        rotateX(center, rx)
        rotateY(center, ry)
        rotateZ(center, rz)
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
    fun scale(sx:Float, sy:Float, sz:Float) {
        x *= sx
        y *= sy
        z *= sz
    }



    //basic internal operations
    operator fun plus(p:XYZ) = XYZ(
        x + p.x,
        y + p.y,
        z + p.z
    )

    operator fun minus(p:XYZ) = XYZ(
        x - p.x,
        y - p.y,
        z - p.z
    )

    operator fun times(p:XYZ) = XYZ(
        x * p.x,
        y * p.y,
        z * p.z
    )

    operator fun div(p:XYZ) = XYZ(
        x / p.x,
        y / p.y,
        z / p.z
    )

    //basic external operations
    operator fun plus(f:Float) = XYZ(
        x + f,
        y + f,
        z + f
    )

    operator fun minus(f:Float) = XYZ(
        x - f,
        y - f,
        z - f
    )

    operator fun times(f:Float) = XYZ(
        x * f,
        y * f,
        z * f
    )

    operator fun div(f:Float) = XYZ(
        x / f,
        y / f,
        z / f
    )



    //display
    fun toStr() = "($x,$y,$z)"



    //static operations
    companion object {

        //scalar
        fun scalProd(p1:XYZ, p2:XYZ) = (
                p1.x*p2.x +
                p1.y*p2.y +
                p1.z*p2.z
        )

        //vectorial product
        fun vectProd(p1:XYZ, p2:XYZ) = XYZ(
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
