package fr.stark.steauc.gl



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



    //position
    fun translate(dx:Float, dy:Float, dz:Float) {
        x += dx
        y += dy
        z += dz
    }



    //rotations
    fun rotateX(cosA:Double, sinA:Double) : XYZ {

        //new coordinates
        val newY = (y*cosA - z*sinA).toFloat()
        val newZ = (y*sinA + z*cosA).toFloat()

        return XYZ(x, newY, newZ)
    }

    fun rotateY(cosA:Double, sinA:Double) : XYZ {

        //new coordinates
        val newX = (z*sinA + x*cosA).toFloat()
        val newZ = (z*cosA - x*sinA).toFloat()

        return XYZ(newX, y, newZ)
    }

    fun rotateZ(cosA:Double, sinA:Double) : XYZ {

        //new coordinates
        val newX = (x*cosA - y*sinA).toFloat()
        val newY = (x*sinA + y*cosA).toFloat()

        return XYZ(newX, newY, z)
    }



    //scale
    fun scale(scaleX:Float, scaleY:Float, scaleZ:Float) {
        x *= scaleX
        y *= scaleY
        z *= scaleZ
    }
}
