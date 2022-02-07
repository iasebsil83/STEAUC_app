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
                super.log(CODEINFO__FATAL_ERROR, message)
            }

            //runtime
            else -> {
                super.log(CODEINFO__RUNTIME_ERROR, message)
            }
        }
    }
}