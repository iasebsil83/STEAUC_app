package fr.stark.steauc

import android.content.Context
import android.util.Log
import fr.stark.steauc.gl.Plak
import fr.stark.steauc.gl.XYZ
import fr.stark.steauc.log.CODEINFO__RUNTIME_ERROR
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.CodeInfo
import java.lang.NumberFormatException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
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
            var index = 0
            var lineData = arrayOfNulls<String>(4)
            readFile(givenContext, path) { line ->

                // Get values which belong to it's type
                if(line.startsWith("vertex ")){
                    lineData[index] = line
                }
                else if(line.startsWith("normal ")){
                    lineData[index] = line
                }
                index++

                // Create & add plak to the list
                if(index >= 4){ // 3 Vertex lines + 1 normal line

                    val pn  = lineData[0]?.split(" ")
                    val pv1 = lineData[1]?.split(" ")
                    val pv2 = lineData[2]?.split(" ")
                    val pv3 = lineData[3]?.split(" ")
                    try {
                        if(pv1 != null && pv2 != null && pv3 != null && pn != null){
                            plakList.add(
                                Plak(
                                    // Vertices
                                    XYZ(pv1[1].toFloat(), pv1[2].toFloat(), pv1[3].toFloat()),
                                    XYZ(pv2[1].toFloat(), pv2[2].toFloat(), pv2[3].toFloat()),
                                    XYZ(pv3[1].toFloat(), pv3[2].toFloat(), pv3[3].toFloat()),

                                    // Normals
                                    XYZ(pn[1].toFloat(), pn[2].toFloat(), pn[3].toFloat()),
                                    XYZ(pn[1].toFloat(), pn[2].toFloat(), pn[3].toFloat()),
                                    XYZ(pn[1].toFloat(), pn[2].toFloat(), pn[3].toFloat()),
                                )
                            )
                        }
                    } catch (e: NumberFormatException) {
                        val info = CodeInfo("Utils", "gl/Utils.kt")
                        Error(info).log(
                            CODEINFO__RUNTIME_ERROR,
                            "Incorrect line in STL file ${path}."
                        )
                    }

                    lineData[0] = null // null for security (to float)
                    lineData[1] = null
                    lineData[2] = null
                    lineData[3] = null
                    index = 0
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
