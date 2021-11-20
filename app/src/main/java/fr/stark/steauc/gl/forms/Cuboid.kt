package fr.stark.steauc.gl.forms

import fr.stark.steauc.gl.Color
import fr.stark.steauc.gl.Plak
import fr.stark.steauc.gl.PlakObject
import fr.stark.steauc.gl.XYZ



class Cuboid(
    givenWidth  : Float,
    givenHeight : Float,
    givenDepth  : Float,
    givenColor  : Color
) : PlakObject(mutableListOf(), givenColor) {

    //dimensions
    private val width  : Float = givenWidth
    private val height : Float = givenHeight
    private val depth  : Float = givenDepth



    //init
    init{
        val w_2 = width/2
        val h_2 = height/2
        val d_2 = depth/2

        /* ------------------------------------------------

                   Coordinates plan
                   ----------------

                  w
              <-------->

          ^   E--------F   ^
        h |   |\       |\   \
          |   | \      | \   \
          v   G--\-----H  \   \ d
               \  \     \  \   \
                \  \     \  \   \      -->
                 \  \     \  \   \      z
                  \  A--------B   v     ^
                   \ |      \ |          \
                    \|       \|           \        -->
                     C--------D            X------> x
                                           |
            A(-w_2, -h_2, -d_2)            |
            B( w_2, -h_2, -d_2)            | -->
            C(-w_2,  h_2, -d_2)            v  y
            D( w_2,  h_2, -d_2)

            E(-w_2, -h_2,  d_2)
            F( w_2, -h_2,  d_2)
            G(-w_2,  h_2,  d_2)
            H( w_2,  h_2,  d_2)
        ------------------------------------------------ */

        //FRONT - BACK

        //front face
        plakList.add(
            Plak(
                XYZ(-w_2, -h_2, -d_2), //A
                XYZ( w_2, -h_2, -d_2), //B
                XYZ(-w_2,  h_2, -d_2)  //C
            )
        )
        plakList.add(
            Plak(
                XYZ( w_2, -h_2, -d_2), //B
                XYZ(-w_2,  h_2, -d_2), //C
                XYZ( w_2,  h_2, -d_2)  //D
            )
        )

        //back face
        plakList.add(
            Plak(
                XYZ(-w_2, -h_2, d_2), //E
                XYZ( w_2, -h_2, d_2), //F
                XYZ(-w_2,  h_2, d_2)  //G
            )
        )
        plakList.add(
            Plak(
                XYZ( w_2, -h_2, d_2), //F
                XYZ(-w_2,  h_2, d_2), //G
                XYZ( w_2,  h_2, d_2)  //H
            )
        )


        //UP - DOWN

        //up face
        plakList.add(
            Plak(
                XYZ(-w_2, -h_2, -d_2), //A
                XYZ( w_2, -h_2, -d_2), //B
                XYZ(-w_2, -h_2,  d_2)  //E
            )
        )
        plakList.add(
            Plak(
                XYZ( w_2, -h_2, -d_2), //B
                XYZ(-w_2, -h_2,  d_2), //E
                XYZ( w_2, -h_2,  d_2)  //F
            )
        )


        //LEFT - RIGHT

        //left face
        plakList.add(
            Plak(
                XYZ(-w_2,  h_2, -d_2), //C
                XYZ(-w_2,  h_2,  d_2), //G
                XYZ(-w_2, -h_2, -d_2)  //A
            )
        )
        plakList.add(
            Plak(
                XYZ(-w_2,  h_2,  d_2), //G
                XYZ(-w_2, -h_2, -d_2), //A
                XYZ(-w_2, -h_2,  d_2)  //E
            )
        )

        //right face
        plakList.add(
            Plak(
                XYZ(w_2,  h_2, -d_2), //D
                XYZ(w_2, -h_2, -d_2), //B
                XYZ(w_2,  h_2,  d_2)  //H
            )
        )
        plakList.add(
            Plak(
                XYZ(w_2, -h_2, -d_2), //B
                XYZ(w_2,  h_2,  d_2), //H
                XYZ(w_2, -h_2,  d_2)  //F
            )
        )
    }



    //getters
    fun getWidth()  = width
    fun getHeight() = height
    fun getDepth()  = depth
}
