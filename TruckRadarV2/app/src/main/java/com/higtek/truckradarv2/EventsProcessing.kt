package com.higtek.truckradarv2

import android.util.Log
import androidx.compose.runtime.toMutableStateList


public val SERVER_DELAY = 5000L
public val SERVER_DELAY_LONG = 21000L


public suspend fun getEvents(hgcUser: String, hgcPassword: String, mainDataClass: MainDataClass) : Boolean{

    try{
        Log.v("getEvents", "Start")

        var eventsString = HgcApi.getRetrofitService().GetEventParsedData(hgcUser, hgcPassword, 100)
        if (eventsString.isNullOrEmpty()){
            return false
        }
        Log.v("getEvents", "Start processing")
        eventsString = eventsString.replace("&lt;", "<").replace("&gt;", ">").
        replace("<string>", "").replace("</string>", "")

        lateinit var events:Events
        events = SerializationHelper.getInstance().DeserializeEvents(eventsString)
        if (events == null){
            return false
        }

        if (events.Events == null){
            return false
        }

        val truckPosCopy = mainDataClass.truckPositions.toMutableStateList()

        for (ev: AVLEvent in events.Events){
            if ((ev.LatitudeFloat.isNaN()) || (ev.LongitudeFloat.isNaN()))
                continue

            if (!truckPosCopy.any { m -> m.uid == ev.Uid }){
                val truckPos = TruckPosition()
                truckPos.uid = ev.Uid
                truckPos.setEvent(ev)
                truckPos.truckPhotoResourceId = getPhotoResId()
                truckPosCopy.add(truckPos)
            }
            else{
                val oldMarker = truckPosCopy.first { m -> m.uid == ev.Uid }
                oldMarker.setEvent(ev)
            }
        }

        mainDataClass.truckPositions = truckPosCopy

        Log.v("getEvents", "Stop")
    }catch(t: Throwable){
        t.printStackTrace()
        return false
    }

    return true
}

fun getPhotoResId(): Int {

    val rnds = (1..11).random()

    when(rnds){
        1 -> return R.drawable.truck1
        2 -> return R.drawable.truck2
        3 -> return R.drawable.truck3
        4 -> return R.drawable.truck4
        5 -> return R.drawable.truck5
        6 -> return R.drawable.truck6
        7 -> return R.drawable.truck7
        8 -> return R.drawable.truck8
        9 -> return R.drawable.truck9
        10 -> return R.drawable.truck10
        11 -> return R.drawable.truck11
        else -> return R.drawable.truck1
    }
}


public suspend fun clearEvents(hgcUser: String, hgcPassword: String){
    try{
        Log.v("clearEvents", "Clear events")

        var result = HgcApi.getRetrofitService().EraseUnsentEventsForUser(hgcUser, hgcPassword)

        Log.v("clearEvents", "Clear events result: " + result)

    }catch(t: Throwable){
        t.printStackTrace()
    }
}

public suspend fun sendSetCommand(hgcUser: String, hgcPassword: String, currentTruckPos: TruckPosition) : String{

    var result : String = "Failed"

    try{
        Log.v("sendSetCommand", "Send Set Command")

        // 0x17 - EXECUTE SET Command
        // 0x0B 0x5C - SET Stamp
        // List of Seals empty
        result = HgcApi.getRetrofitService().ExecuteCommand(hgcUser, hgcPassword,
            currentTruckPos.uid, currentTruckPos.event.Protocol, 0xFF, "170B5C")

        Log.v("sendSetCommand", "Send Set Command result: " + result)

    }catch(t: Throwable){
        t.printStackTrace()
    }

    return result
}
