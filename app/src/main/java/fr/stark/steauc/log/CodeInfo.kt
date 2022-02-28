package fr.stark.steauc.log

import android.annotation.SuppressLint
import android.util.Log



//log types
const val CODEINFO__MESSAGE       = 0
const val CODEINFO__RUNTIME_ERROR = 1
const val CODEINFO__FATAL_ERROR   = 2



open class CodeInfo {

    //names
    var actorName    : String
    var fileName     : String
    var functionName = ""



    //init
    constructor() {
        actorName = "UNDEFINED"
        fileName  = "UNDEFINED"
    }

    constructor(givenActorName:String, givenFileName:String) {
        actorName = givenActorName
        fileName  = givenFileName
    }

    //change current function name
    fun function(name:String) {
        functionName = name
    }



    //log
    @SuppressLint("LongLogTag")
    fun log(type:Int, message:String) {
        when(type) {
            CODEINFO__MESSAGE       -> Log.i(".","        $actorName > $functionName() : $message")
            CODEINFO__RUNTIME_ERROR -> Log.i(".","        RUNTIME ERROR > $fileName : $functionName() : $message")
            CODEINFO__FATAL_ERROR   -> Log.i(".","        FATAL ERROR > $fileName : $functionName() : $message")
            else                    -> Log.i(".","        RUNTIME ERROR > CodeInfo.kt : log() : Invalid log type (number $type).")
        }
    }
}
