package fr.stark.steauc.log

import android.annotation.SuppressLint

class Message(info:CodeInfo) : CodeInfo() {

    //init
    init {
        actorName = info.actorName
        fileName  = info.fileName
    }

    //log
    @SuppressLint("LongLogTag")
    fun log(message:String) {
        super.log(CODEINFO__MESSAGE, message)
    }
}
