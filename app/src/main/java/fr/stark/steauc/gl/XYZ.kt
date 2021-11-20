package fr.stark.steauc.gl

import kotlin.math.cos
import kotlin.math.sin



//memory allocation
const val FLOAT_STRIDE : Int = 4 //4 bytes
const val XYZ_STRIDE   : Int = 3 * FLOAT_STRIDE



class XYZ { // <=> vertex

    //coordinates
    private var x : Float
    private var y : Float
    private var z : Float



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



    //getters
    fun getX() = x
    fun getY() = y
    fun getZ() = z



    //movements
    fun translate(dx:Float, dy:Float, dz:Float) {
        x += dx
        y += dy
        z += dz
    }
    fun rotate(center:XYZ, angleX:Double, angleY:Double, angleZ:Double) {
        rotateX(center, angleX)
        rotateY(center, angleY)
        rotateZ(center, angleZ)
    }



    //axial rotations
    fun rotateX(center:XYZ, angle:Double) {
        val cosA = cos(angle)
        val sinA = sin(angle)

        rotateX(center, cosA,sinA)
    }

    fun rotateY(center:XYZ, angle:Double) {
        val cosA = cos(angle)
        val sinA = sin(angle)

        rotateY(center, cosA,sinA)
    }

    fun rotateZ(center:XYZ, angle:Double) {
        val cosA = cos(angle)
        val sinA = sin(angle)

        rotateZ(center, cosA,sinA)
    }



    //optimizated axial rotations (avoid cos & sin calculations)
    fun rotateX(center:XYZ, cosA:Double, sinA:Double) {
        val dy = y - center.getY()
        val dz = z - center.getZ()

        //new coordinates
        y = center.getY() + (dy*cosA - dz*sinA).toFloat()
        z = center.getZ() + (dy*sinA + dz*cosA).toFloat()
    }

    fun rotateY(center:XYZ, cosA:Double, sinA:Double) {
        val dx = x - center.getX()
        val dz = z - center.getZ()

        //new coordinates
        x = center.getX() + (dz*sinA + dx*cosA).toFloat()
        z = center.getZ() + (dz*cosA - dx*sinA).toFloat()
    }

    fun rotateZ(center:XYZ, cosA:Double, sinA:Double) {
        val dx = x - center.getX()
        val dy = y - center.getY()

        //new coordinates
        x = center.getX() + (dx*cosA - dy*sinA).toFloat()
        y = center.getY() + (dx*sinA + dy*cosA).toFloat()
    }



    //scale
    fun scale(scaleX:Float, scaleY:Float, scaleZ:Float) {
        x *= scaleX
        y *= scaleY
        z *= scaleZ
    }
}
