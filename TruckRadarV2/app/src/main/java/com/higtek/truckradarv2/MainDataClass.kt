package com.higtek.truckradarv2

import androidx.compose.runtime.mutableStateListOf
import com.google.maps.android.compose.Marker

public class TruckPosition(){
    public lateinit var uid : String
    public lateinit var event : AVLEvent

    public fun getSnippet() : String {
        if(event == null)
            return ""

        var snp = "Time: " + event.DateTime + "\n" +
                "Latitude: " + event.LatitudeFloat + "\n" +
                "Longitude: " + event.LongitudeFloat + "\n" +
                "Speed: " + event.Speed + "\n" +
                "Course: " + event.Course

        return snp
    }
}



data class MainDataClass (var dummy: Boolean = false) {

    //var tags = mutableStateListOf<String>("EMPTY")
    //var listOfUserNames = mutableStateListOf<String>("-")
    //var listOfUserNames = mutableStateListOf<String>("One", "Two", "Four", "Four1", "Four2")
    //var userName : String = ""

    //var listOfMhhtUsers = mutableStateListOf<MHHTUser>()

    var truckPositions = mutableStateListOf<TruckPosition>()
}