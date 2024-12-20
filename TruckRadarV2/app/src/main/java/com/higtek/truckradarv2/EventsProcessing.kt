package com.higtek.truckradarv2

import android.util.Log
import androidx.compose.runtime.toMutableStateList


public val SERVER_DELAY = 1000L
public val SERVER_DELAY_LONG = 5000L


public suspend fun getEvents(hgcUser: String, hgcPassword: String, mainDataClass: MainDataClass) : Boolean{

    try{
        //Log.v("getEvents", "Start")

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
                truckPos.event = ev
                truckPosCopy.add(truckPos)
            }
            else{
                val oldMarker = truckPosCopy.first { m -> m.uid == ev.Uid }
                oldMarker.event = ev
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

/*
private fun getEvents(hgcUser: String, hgcPassword: String){
    label@coroutineScope.launch {

        try{
            //Log.v("getEvents", "Start")

            var eventsString = HgcApi.getRetrofitService().GetEventParsedData(hgcUser, hgcPassword, 100)
            if (eventsString.isNullOrEmpty()){
                Handler().postDelayed(worker, DELAY_LONG)
                return@label
            }
            Log.v("getEvents", "Start processing")
            eventsString = eventsString.replace("&lt;", "<").replace("&gt;", ">").
            replace("<string>", "").replace("</string>", "")

            lateinit var events:Events
            events = SerializationHelper.getInstance().DeserializeEvents(eventsString)
            if (events == null){
                Handler().postDelayed(worker, DELAY_LONG)
                return@label
            }

            if (events.Events == null){
                Handler().postDelayed(worker, DELAY_LONG)
                return@label
            }

            for (ev: AVLEvent in events.Events){
                if ((ev.LatitudeFloat.isNaN()) || (ev.LongitudeFloat.isNaN()))
                    continue

                if (mMarkers.size <= 0){
                    addMarkerToList(ev)
                    var location = LatLng(ev.LatitudeFloat, ev.LongitudeFloat)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                    continue
                }

                if (!mMarkers.any { m -> m.uid == ev.Uid }){
                    addMarkerToList(ev)
                }
                else{
                    val oldMarker = mMarkers.first { m -> m.uid == ev.Uid }
                    oldMarker.marker.position = LatLng(ev.LatitudeFloat, ev.LongitudeFloat)
                    oldMarker.marker.snippet = generateSnippet(ev)
                }
            }

            Log.v("getEvents", "Stop")
        }catch(t: Throwable){
            t.printStackTrace()
        }
        Handler().postDelayed(worker, DELAY)
    }
}
*/

/*
private fun clearEvents(){
    coroutineScope.launch {
        try{
            Log.v("clearEvents", "Clear events")

            //var result = HgcApi.getRetrofitService().EraseUnsentEventsForUser(mHgcUser, mHgcPassword)

        }catch(t: Throwable){
            t.printStackTrace()
        }

    }
}
*/