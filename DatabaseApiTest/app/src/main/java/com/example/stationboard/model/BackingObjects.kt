package com.example.stationboard.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SBBTransport(val stationboard : MutableList<StationboardEntry>) {}

class StationboardEntry(val to : String, val category: String, val number : String?
, val stop  : StationboardEntryStop) {
    val train : String
        get() {
            return category + (number ?: "")
        }

    val dateTime : String
        get() {
            return getDateTime(stop.departureTimestamp)
        }

    fun getDateTime(l: Long): String {
        try {
            val sdf = SimpleDateFormat("HH:mm",
                Locale.getDefault())
            val netDate = Date(l * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}

class StationboardEntryStop(val platform : String, val departureTimestamp : Long) {}