package com.example.stationboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stationboard.model.AppViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun DetailView (navController: NavHostController,
                modifier: Modifier = Modifier,
                index : Int,
                model: AppViewModel = viewModel()) /*prende un istanza del mio model*/ {
    val entry = model.entries[index]
    Column(modifier = modifier) {
        Text("Detail Page", fontSize = 24.sp)
        Text(entry.train, fontSize = 24.sp)
        Text(entry.to, fontSize = 24.sp)
        MyMap()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyMap() {
    // Definisci la posizione della fotocamera
    val singapore = LatLng(1.3521, 103.8198)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    // Aggiungi GoogleMap Composable
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    )
}