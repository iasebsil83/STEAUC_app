package fr.stark.steauc

import android.util.Log


class Ename {

    //collections
    private var names   = mutableListOf<String>()
    private var indexes = mutableListOf<Int>()
    var length          = 0

    //init
    constructor() {}

    constructor(associations:Map<String,Int>) {
        for(assos in associations) {
            names.add(   assos.key   )
            indexes.add( assos.value )
        }
    }



    //add
    fun add(index:Int, name:String) {
        names.add(name)
        indexes.add(index)
        length++
    }



    //get
    operator fun get(wantedIndex:Int) : String {
        if(wantedIndex in indexes){
            return names[ indexes.indexOf(wantedIndex) ]
        }
        throw IndexOutOfBoundsException()
    }

    operator fun get(wantedName:String) : Int {
        if(wantedName in names){
            return indexes[ names.indexOf(wantedName) ]
        }
        throw IndexOutOfBoundsException()
    }



    //contains
    operator fun contains(index:Int)   = (index in indexes)
    operator fun contains(name:String) = (name in names)
}
