package fr.stark.steauc.kalman

class DataProcessor() {

    //Gyr process data
    //Angular speed
    var XangleSpeed   : Double = 0.0
    var YangleSpeed   : Double = 0.0
    var ZangleSpeed   : Double = 0.0
    //Gyr pos
    var Xangle        : Double = 0.0
    var Yangle        : Double = 0.0
    var Zangle        : Double = 0.0

    //Acc process data
    //Acceleration
    var Xacceleration : Double = 0.0
    var Yacceleration : Double = 0.0
    var Zacceleration : Double = 0.0
    //Speed
    var Xspeed        : Double = 0.0
    var Yspeed        : Double = 0.0
    var Zspeed        : Double = 0.0
    //Position
    var Xpos          : Double = 0.0
    var Ypos          : Double = 0.0
    var Zpos          : Double = 0.0

    fun computeAngle(datas: MutableList<DataSet>){
        
    }

    fun computePos(datas: MutableList<DataSet>){

    }
}