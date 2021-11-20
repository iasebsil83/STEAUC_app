package fr.stark.steauc.gl



//memory allocation
const val PLAK_STRIDE : Int = 3 * XYZ_STRIDE



class Plak{

    //points
    val p1 : XYZ
    val p2 : XYZ
    val p3 : XYZ



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
}
