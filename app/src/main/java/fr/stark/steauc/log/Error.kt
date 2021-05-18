package fr.stark.steauc.log

import android.annotation.SuppressLint



class Error(info: CodeInfo) : CodeInfo(
    info.getActorName(),
    info.getFileName(),
    info.getFunctionName()
) {



    //log
    @SuppressLint("LongLogTag")
    fun log(fatal: Boolean, message: String) {
        when {
            //fatal error
            fatal -> {
                //Log.i("    UselessMessage > log() ", "A FATAL ERROR has occured {")
                super.log(CODEINFO__FATAL_ERROR, message)
                //Log.i("    UselessMessage > log() ", "}")
            }

            //runtime
            else -> {
                //Log.i("    UselessMessage > log() ", "A RUNTIME ERROR has occurred {")
                super.log(CODEINFO__RUNTIME_ERROR, message)
                //Log.i("    UselessMessage > log() ", "}")
            }
        }
    }
}