package fr.stark.steauc.gl


//standard colors
val RED     = Color(255,  0,  0, 255)
val GREEN   = Color(  0,255,  0, 255)
val BLUE    = Color(  0,  0,255, 255)
val CYAN    = Color(  0,255,255, 255)
val YELLOW  = Color(255,255,  0, 255)
val MAGENTA = Color(255,  0,255, 255)
val WHITE   = Color(255,255,255, 255)
val BLACK   = Color(  0,  0,  0, 255)



class Color(givenRed:Int, givenGreen:Int, givenBlue:Int, givenAlpha:Int){

    //coordinates
    private val red   : Float = givenRed.toFloat()   / 255f
    private val green : Float = givenGreen.toFloat() / 255f
    private val blue  : Float = givenBlue.toFloat()  / 255f
    private val alpha : Float = givenAlpha.toFloat() / 255f



    //getters
    fun getRed()   = red
    fun getGreen() = green
    fun getBlue()  = blue
    fun getAlpha() = alpha
}
