package fr.stark.steauc.kalman

import android.util.Log

//SENSITIVITY
const val ACC_SENSITIVITY   = 0.061
const val GYR_SENSITIVITY   = 4.375

//CALIBRATION
const val calibrationSample = 20


class DataProcessor() {

    //Calibration
    var calibrationPointsNbr  : Int    = 0
    var XAngleSpeedCalib      : Double = 0.0
    var YAngleSpeedCalib      : Double = 0.0
    var ZAngleSpeedCalib      : Double = 0.0
    var XAccelerometerCalib   : Double = 0.0
    var YAccelerometerCalib   : Double = 0.0
    var ZAccelerometerCalib   : Double = 0.0

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


    fun applyCalibration(points: MutableList<Double>): MutableList<Double> {
        points[0] -= XAccelerometerCalib
        points[1] -= YAccelerometerCalib
        points[2] -= ZAccelerometerCalib
        points[3] -= XAngleSpeedCalib
        points[4] -= YAngleSpeedCalib
        points[5] -= ZAngleSpeedCalib

        return points
    }

    fun computeAngle(data: MutableList<DataSet>){
        var lastIndex = data.lastIndex

        XangleSpeed = data[lastIndex].points[3]
        YangleSpeed = data[lastIndex].points[4]
        ZangleSpeed = data[lastIndex].points[5]

        //If at least 2 values, compute an angle
        if(lastIndex > 0){
            Log.i("Kalman", Xangle.toString())
            Xangle += data[lastIndex].points[3]*data[lastIndex].elapsedTime/GYR_SENSITIVITY
            Yangle += (data[lastIndex].points[4]-data[lastIndex-1].points[4])/data[lastIndex].elapsedTime
            Zangle += (data[lastIndex].points[5]-data[lastIndex-1].points[5])/data[lastIndex].elapsedTime
        }
    }

    fun computePos(data: MutableList<DataSet>){
        var lastIndex = data.lastIndex
        //Save actual acceleration speed in data model
        Xacceleration = data[lastIndex].points[0]
        Yacceleration = data[lastIndex].points[1]
        Zacceleration = data[lastIndex].points[2]
        if(lastIndex < 0){
            Xspeed += (data[lastIndex].points[0]-data[lastIndex-1].points[0])/data[lastIndex].elapsedTime
            Yspeed += (data[lastIndex].points[1]-data[lastIndex-1].points[1])/data[lastIndex].elapsedTime
            Zspeed += (data[lastIndex].points[2]-data[lastIndex-1].points[2])/data[lastIndex].elapsedTime
        }
    }

    fun calibration(data: MutableList<DataSet>): Boolean {
        if(calibrationPointsNbr < calibrationSample){
            var tempData = data.last()

            XAccelerometerCalib += tempData.points[0]
            YAccelerometerCalib += tempData.points[1]
            ZAccelerometerCalib += tempData.points[2]

            XAngleSpeedCalib += tempData.points[3]
            YAngleSpeedCalib += tempData.points[4]
            ZAngleSpeedCalib += tempData.points[5]

            return false
        }
        else{
            XAccelerometerCalib /= calibrationSample
            YAccelerometerCalib /= calibrationSample
            ZAccelerometerCalib /= calibrationSample

            XAngleSpeedCalib /= calibrationSample
            YAngleSpeedCalib /= calibrationSample
            ZAngleSpeedCalib /= calibrationSample

            return true
        }
    }
}