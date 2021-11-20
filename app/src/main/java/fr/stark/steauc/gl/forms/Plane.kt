package fr.stark.steauc.gl.forms

import fr.stark.steauc.gl.Color
import fr.stark.steauc.gl.Plak
import fr.stark.steauc.gl.PlakObject
import fr.stark.steauc.gl.XYZ



class Plane(
    givenWidth  : Float,
    givenDepth  : Float,
    givenColor  : Color
) : PlakObject(mutableListOf(), givenColor) {

    //dimensions
    private val width  : Float = givenWidth
    private val depth  : Float = givenDepth



    //init
    init{
        val w_2 = width/2
        val d_2 = depth/2

        /* ------------------------------------------------

                   Coordinates plan
                   ----------------

                  w
              <------->

              D-------C   ^
               \       \   \ d    -->
                \       \   \      z
                 A-------B   v     ^
                                    \      -->
                                     X----> x
            A(-w_2, -h_2, -d_2)      |
            B( w_2, -h_2, -d_2)      |  -->
            D(-w_2, -h_2,  d_2)      v   y
            C( w_2, -h_2,  d_2)
        ------------------------------------------------ */

        //face
        plakList.add(
            Plak(
                XYZ(-w_2, 0f, -d_2), //A
                XYZ( w_2, 0f, -d_2), //B
                XYZ(-w_2, 0f,  d_2)  //D
            )
        )
        plakList.add(
            Plak(
                XYZ( w_2, 0f, -d_2), //B
                XYZ(-w_2, 0f,  d_2), //D
                XYZ( w_2, 0f,  d_2)  //C
            )
        )
    }



    //getters
    fun getWidth()  = width
    fun getDepth()  = depth
}
