package com.higtek.truckradarv2

import androidx.compose.runtime.mutableStateListOf
import com.google.maps.android.compose.Marker

public class SealStatus(){
    public var sealId : String = ""
    public var sealStatus : Int = 0
    public var statusDescription : String = ""
}

public class TruckPosition(){
    public lateinit var uid : String
    public lateinit var event : AVLEvent
        private set
    public var truckPhotoResourceId : Int = 0
    public var sealStatuses : MutableList<SealStatus>? = null


    public fun setEvent(ev : AVLEvent){
        event = ev

        try {
            // Process Read Seal Status event.
            if((event.Code - 100) == 0x68){
                var sealStatus = SealStatus()
                sealStatus.sealId = event.Location
                sealStatus.sealStatus = event.ShortStatus
                sealStatus.statusDescription = "(${sealStatus.sealStatus.toString()}) "

                if((sealStatus.sealStatus and 0x0001) > 0)
                    sealStatus.statusDescription += "Missing "
                else{
                    var sensorStatus = (sealStatus.sealStatus shr 0x0002) and 0x0003
                    when(sensorStatus){
                        0 -> sealStatus.statusDescription += "SET "
                        1 -> sealStatus.statusDescription += "Invalid status "
                        2 -> sealStatus.statusDescription += "TAMPERED "
                        3 -> sealStatus.statusDescription += "OPEN "
                    }

                    if((sealStatus.sealStatus and 0x0002) > 0)
                        sealStatus.statusDescription += " LBW"
                }

                var stat = sealStatuses?.find { it.sealId == sealStatus.sealId }
                if(stat == null){
                    if(sealStatuses == null)
                        sealStatuses = mutableStateListOf<SealStatus>()
                    sealStatuses?.add(sealStatus)
                }
                else{
                    stat.sealStatus = sealStatus.sealStatus
                    stat.statusDescription = sealStatus.statusDescription
                }
            }

        }
        catch (t: Throwable){
            t.printStackTrace()
        }
    }

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

    var truckPositions = mutableStateListOf<TruckPosition>()
}