package com.example.stationboard.model

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon

class AppViewModel(application: Application) :
    AndroidViewModel(application) {

var name = mutableStateOf("Initial State")
var entries = mutableStateListOf<StationboardEntry>()

    var station = "Zurich"
    var showDropdown = mutableStateOf<Boolean>(false)
    var stations = listOf<String>("Zurich", "Winterthur")

    init {
        openServer()
    }
    //it is possible to create methods and
    //change attributes within
    fun changeName() {
        //use .value to access the value of the state
        //variable
        name.value = "New Name from Model State"
    }

    fun openServer() { // usato in dropdown!
        val context =  getApplication<Application>().applicationContext
        val requestQueue = Volley.newRequestQueue(context)
//define a request.
        val request = StringRequest(
            Request.Method.GET, "https://transport.opendata.ch/v1/stationboard?station=$station",
            { response ->
                //find the response string in "response"
                //Log.i("Volley", response)
                val sbbTransport = Klaxon().parse<SBBTransport>(response)
                //name.value = sbbTransport!!.stationboard.count().toString()
                entries.clear() //elimino vecchi per crearne di nuovi da dropdown!
                entries.addAll(sbbTransport?.stationboard ?: listOf<StationboardEntry>()) //? x disabilitare controllo null
            },
            Response.ErrorListener {
                Log.e("Volley", "Error loading data: $it")
            })

//add the call to the request queue
        requestQueue.add(request)
    }
}
