package fr.stark.steauc.kalman

import android.util.Log
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import kotlinx.coroutines.delay

const val MAX_DATALIST_SIZE = 20

class Kalman: Thread() {
    var dataSetList    : MutableList<DataSet> = mutableListOf()
    var newDataSet     : Boolean              = false
    var dataProcessor  : DataProcessor        = DataProcessor()
    var calibration    : Boolean              = false

    //Log
    private val info   : CodeInfo             = CodeInfo("Kalman", "Kalman.kt")
    private val msg    : Message              = Message(info)
    private val err    : Error                = Error(info)

    public override fun run() {
        Log.i("KALMAN", "Kalman thread ${currentThread()} has run.")

        while(true){
            //counter++
            if((dataSetList.isNotEmpty())){
                if (newDataSet){
                    if(calibration) {
                        newDataSet = false
                        msg.log("New dataset")
                        dataSetList[dataSetList.lastIndex].points = dataProcessor.applyCalibration(dataSetList[dataSetList.lastIndex].points)
                        dataProcessor.computeAngle(dataSetList)
                        dataProcessor.computePos(dataSetList)
                        clearDataList()
                    }
                    else{
                        calibration = dataProcessor.calibration(dataSetList)
                    }
                }
            }
        }
    }

    private fun clearDataList(){
        if(dataSetList.size < MAX_DATALIST_SIZE){
            dataSetList.removeAt(0)
        }
    }
}
