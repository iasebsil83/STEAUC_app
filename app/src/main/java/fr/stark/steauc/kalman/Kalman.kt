package fr.stark.steauc.kalman

import android.util.Log


class Kalman: Thread() {
    var dataSetList    : MutableList<DataSet> = mutableListOf()
    var newDataSet     : Boolean              = false
    var dataProcessor  : DataProcessor        = DataProcessor()


    public override fun run() {
        Log.i("KALMAN", "Kalman thread ${currentThread()} has run.")

        while(true){
            if(newDataSet && (dataSetList.size < 1)){
                newDataSet = false

                dataProcessor.computeAngle(dataSetList)
                dataProcessor.computePos(dataSetList)

            }
        }
    }
}
