package fr.stark.steauc.kalman;

class DataSet(lastUpdateTime: Long) {
    var currentTime : Long = System.currentTimeMillis()
    var elapsedTime : Long = currentTime - lastUpdateTime
    var points      : MutableList<Double> = mutableListOf()
}
