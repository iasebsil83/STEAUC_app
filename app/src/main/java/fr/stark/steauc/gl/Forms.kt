package fr.stark.steauc.gl

class Forms {

    companion object {

        fun Plane(width:Float, depth:Float) : MutableList<Plak> {
            var plakList = mutableListOf<Plak>()

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
                A(    0, 0,      0)      |
                B(width, 0,      0)      |  -->
                D(    0, 0,  depth)      v   y
                C(width, 0,  depth)
            ------------------------------------------------ */

            //face
            plakList.add(
                Plak(
                    XYZ( 0f, 0f, 0f), //A
                    XYZ( width, 0f, 0f), //B
                    XYZ( 0f, 0f,  depth)  //D
                )
            )
            plakList.add(
                Plak(
                    XYZ( width, 0f, 0f), //B
                    XYZ( 0f, 0f,  depth), //D
                    XYZ( width, 0f,  depth)  //C
                )
            )
            return plakList
        }

        fun Hexaedron(width:Float, height:Float, depth:Float) : MutableList<Plak> {
            var plakList = mutableListOf<Plak>()

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
                A(0, 0, 0)                     |
                B(w, 0, 0)                     | -->
                C(0, h, 0)                     v  y
                D(w, h, 0)

                E(0, 0, d)
                F(w, 0, d)
                G(0, h, d)
                H(w, h, d)
            ------------------------------------------------ */



            //front face
            plakList.add(
                Plak(
                    XYZ(0f, 0f, 0f), //A
                    XYZ(width, 0f, 0f), //B
                    XYZ(0f, height, 0f)  //C
                )
            )
            plakList.add(
                Plak(
                    XYZ(width, 0f, 0f), //B
                    XYZ(width, height, 0f),  //D
                    XYZ(0f, height, 0f) //C
                )
            )

            //right face
            plakList.add(
                Plak(
                    XYZ(width, height, 0f), //D
                    XYZ(width, 0f, 0f), //B
                    XYZ(width, height, depth)  //H
                )
            )
            plakList.add(
                Plak(
                    XYZ(width, 0f, 0f), //B
                    XYZ(width, 0f, depth),  //F
                    XYZ(width, height, depth) //H
                )
            )

           //back face
            plakList.add(
                Plak(
                    XYZ(0f, 0f, depth), //E
                    XYZ(width, 0f, depth), //F
                    XYZ(0f, height, depth)  //G
                )
            )
            plakList.add(
                Plak(
                    XYZ(width, 0f, depth), //F
                    XYZ(width, height, depth),  //H
                    XYZ(0f, height, depth) //G
                )
            )

            //left face
            plakList.add(
                Plak(
                    XYZ(0f, height, 0f), //C
                    XYZ(0f, height, depth), //G
                    XYZ(0f, 0f, 0f)  //A
                )
            )
            plakList.add(
                Plak(
                    XYZ(0f, height, depth), //G
                    XYZ(0f, 0f, depth),  //E
                    XYZ(0f, 0f, 0f) //A
                )
            )

            //up face
            plakList.add(
                Plak(
                    XYZ(0f, 0f, 0f), //A
                    XYZ(width, 0f, 0f), //B
                    XYZ(0f, 0f, depth)  //E
                )
            )
            plakList.add(
                Plak(
                    XYZ(width, 0f, 0f), //B
                    XYZ(width, 0f, depth),  //F
                    XYZ(0f, 0f, depth) //E
                )
            )

            //down face
            plakList.add(
                Plak(
                    XYZ(0f, height, 0f), //C
                    XYZ(0f, height, depth), //G
                    XYZ(width, height, 0f), //D
                )
            )
            plakList.add(
                Plak(
                    XYZ(width, height, 0f), //D
                    XYZ(width, height, depth),  //H
                    XYZ(0f, height, depth) //G
                )
            )

            return plakList
        }

        fun Cube(size:Float) = Hexaedron(size, size, size)
    }
}
