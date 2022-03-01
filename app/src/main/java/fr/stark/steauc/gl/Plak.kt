package fr.stark.steauc.gl



//memory allocation
const val PLAK_STRIDE : Int = 3 * XYZ_STRIDE



class Plak{

    //points
    val p1 : XYZ
    val p2 : XYZ
    val p3 : XYZ

    //normals
    val n1 : XYZ
    val n2 : XYZ
    val n3 : XYZ



    //init
    constructor(){
        p1 = XYZ()
        p2 = XYZ()
        p3 = XYZ()

        n1 = XYZ(-1f, -1f, -1f)
        n2 = XYZ(-1f, -1f, -1f)
        n3 = XYZ(-1f, -1f, -1f)
    }

    constructor(givenP1:XYZ, givenP2:XYZ, givenP3:XYZ){
        p1 = givenP1
        p2 = givenP2
        p3 = givenP3

        n1 = XYZ(-1f, -1f, -1f)
        n2 = XYZ(-1f, -1f, -1f)
        n3 = XYZ(-1f, -1f, -1f)
    }

    constructor(givenP1:XYZ, givenP2:XYZ, givenP3:XYZ, givenN1:XYZ, givenN2:XYZ, givenN3:XYZ){
        p1 = givenP1
        p2 = givenP2
        p3 = givenP3

        n1 = givenN1
        n2 = givenN2
        n3 = givenN3
    }



    //print
    fun toStr() = "{ ${p1.toStr()}, ${p1.toStr()}, ${p1.toStr()} }"
}
