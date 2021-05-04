package fr.stark.steauc.log

import android.annotation.SuppressLint



class Message(info: CodeInfo) : CodeInfo(
    info.getActorName(),
    info.getFileName(),
    info.getFunctionName()
) {

    //log
    @SuppressLint("LongLogTag")
    fun log(message: String) {
        //Log.i("    UselessMessage > log() ", "A MESSAGE has occured {")
        super.log(CODEINFO__MESSAGE, message)
        //Log.i("    UselessMessage > log() ", "}")
    }
}