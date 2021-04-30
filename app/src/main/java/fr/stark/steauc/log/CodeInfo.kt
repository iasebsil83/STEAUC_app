package fr.stark.steauc.log

import android.annotation.SuppressLint
import android.util.Log



open class CodeInfo(
    private var actorName: String,
    private var fileName: String
) {

    //function location
    private lateinit var functionName: String

    //log types
    val CODEINFO__MESSAGE       = 0
    val CODEINFO__RUNTIME_ERROR = 1
    val CODEINFO__FATAL_ERROR   = 2



    //alternative constructor (for setting all CodeInfo at once)
    constructor(actorName: String, fileName: String, functionName: String) : this(actorName, fileName){
        this.actorName    = actorName
        this.fileName     = fileName
        this.functionName = functionName
    }



    //getters
    fun getActorName() = actorName
    fun getFileName() = fileName
    fun getFunctionName() = functionName

    //setters
    fun setFunctionName(name: String){
        functionName = name
    }



    //log
    @SuppressLint("LongLogTag")
    fun log(type: Int, message: String) {
        when(type) {
            CODEINFO__MESSAGE       -> Log.i("","        $actorName > $functionName() : $message")
            CODEINFO__RUNTIME_ERROR -> Log.i("","        RUNTIME ERROR > $fileName : $functionName() : $message")
            CODEINFO__FATAL_ERROR   -> Log.i("","        FATAL ERROR > $fileName : $functionName() : $message")
            else                    -> Log.i("","        RUNTIME ERROR > CodeInfo.kt : log() : Invalid log type (number $type).")
        }
    }
}