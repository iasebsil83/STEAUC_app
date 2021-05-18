package fr.stark.steauc.gl



class XYZ { // <=> vertex



    //coordinates
    private val x : Float
    private val y : Float
    private val z : Float



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
}
