package fr.stark.steauc.log

import android.annotation.SuppressLint

class Error(info:CodeInfo) : CodeInfo() {

    //init
    init {
        actorName = info.actorName
        fileName  = info.fileName
    }

    //log
    @SuppressLint("LongLogTag")
    fun log(fatal:Boolean, message:String) {
        when {
            //fatal error
            fatal -> {
                super.log(CODEINFO__FATAL_ERROR, message)
            }

            //runtime
            else -> {
                super.log(CODEINFO__RUNTIME_ERROR, message)
            }
        }
    }
}