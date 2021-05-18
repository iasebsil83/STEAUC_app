package fr.stark.steauc.gl



class Plak{



    //points
    private val p1 : XYZ
    private val p2 : XYZ
    private val p3 : XYZ



    //init
    constructor(){
        p1 = XYZ()
        p2 = XYZ()
        p3 = XYZ()
    }

    constructor(givenP1:XYZ, givenP2:XYZ, givenP3:XYZ){
        p1 = givenP1
        p2 = givenP2
        p3 = givenP3
    }



    //getters
    fun getP1() = p1
    fun getP2() = p2
    fun getP3() = p3
}
