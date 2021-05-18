package fr.stark.steauc.gl



class Color {



    //coordinates
    private var red   = 0f
    private var green = 0f
    private var blue  = 0f
    private var alpha = 1f



    //init
    constructor()

    constructor(givenRed:Int, givenGreen:Int, givenBlue:Int, givenAlpha:Int){
        red   = givenRed  /255f
        green = givenGreen/255f
        blue  = givenBlue /255f
        alpha = givenAlpha/255f
    }



    //getters
    fun getRed()   = red
    fun getGreen() = green
    fun getBlue()  = blue
    fun getAlpha() = alpha
}
