package fr.stark.steauc

import android.content.Context
import fr.stark.steauc.gl.Plak
import fr.stark.steauc.gl.XYZ
import fr.stark.steauc.log.CODEINFO__RUNTIME_ERROR
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.CodeInfo
import java.lang.NumberFormatException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.*



class Utils {

    //useful
    companion object {

        //files
        fun readFile(givenContext:Context, path:String, action:(String) -> Unit){
            val scanner = Scanner(givenContext.assets.open(path))

            //for each line : do something with it
            while(scanner.hasNextLine()){
                action( scanner.nextLine() )
            }
            scanner.close()
        }

        fun readSTL(givenContext:Context, path:String) : MutableList<Plak> {
            var plakList = mutableListOf<Plak>()
            var lineParity = 1
            var line1 = ""
            var line2 = ""
            readFile(givenContext, path) { line ->

                //get only vertices
                if (line.startsWith("vertex ")) {

                    //store 1st line
                    if (lineParity == 1) {
                        line1 = line
                        lineParity = 2

                        //store 2nd line
                    } else if (lineParity == 2) {
                        line2 = line
                        lineParity = 3

                        //add plak
                    } else {
                        val sl1 = line1.split(" ")
                        val sl2 = line2.split(" ")
                        val sl3 = line.split(" ")
                        try {
                            plakList.add(
                                Plak(
                                    XYZ(sl1[1].toFloat(), sl1[2].toFloat(), sl1[3].toFloat()),
                                    XYZ(sl2[1].toFloat(), sl2[2].toFloat(), sl2[3].toFloat()),
                                    XYZ(sl3[1].toFloat(), sl3[2].toFloat(), sl3[3].toFloat())
                                )
                            )
                        } catch (e: NumberFormatException) {
                            val info = CodeInfo("Utils", "gl/Utils.kt")
                            Error(info).log(
                                CODEINFO__RUNTIME_ERROR,
                                "Incorrect line in STL file ${path}."
                            )
                        }

                        lineParity = 1
                    }
                }
            }
            return plakList
        }



        //buffers
        fun FloatBuffer(size:Int) : FloatBuffer {

            //allocate as primitive buffer
            val byteBuffer = ByteBuffer.allocateDirect(size)
            byteBuffer.order(ByteOrder.nativeOrder())

            //convert into FloatBuffer
            return byteBuffer.asFloatBuffer()
        }
    }
}
