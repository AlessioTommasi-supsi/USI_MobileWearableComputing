package com.example.stationboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stationboard.model.AppViewModel
import com.example.stationboard.ui.theme.StationboardTheme

//googlemaps
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RequestLocationPermission()

            StationboardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val model : AppViewModel = AppViewModel(this.application)
                    NavHost(navController = navController, startDestination = "start") {
                        composable("start") {
                            //ListView(navController = navController, model = model) // non va

                            StationboardList(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding).padding(),
                                model = model
                            )
                        }
                        //mi definisco routes!
                        composable("detail/{index}",
                            arguments = listOf(navArgument("index") { type = NavType.IntType }))
                        { backStackEntry ->
                            val index = backStackEntry.arguments!!.getInt("index")
                            DetailView(navController = navController,
                                model = model,
                                index = index,
                                modifier = Modifier.padding(innerPadding).padding())
                        }
                        composable("barzigola/{index}", // se non metto index crasha perche: val index = backStackEntry.arguments!!.getInt("index")  ritorna null!
                            arguments = listOf(navArgument("index") { type = NavType.IntType }))
                        { backStackEntry ->
                            val index = backStackEntry.arguments!!.getInt("index")
                            //è DetailView.kt devo chiamarla col nome giusto!
                            DetailView(navController = navController,
                                model = model,
                                index = index,
                                modifier = Modifier.padding(innerPadding).padding())
                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission() {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
        LaunchedEffect(Unit) {
            locationPermissionState.launchPermissionRequest()
        }
    } else {
        MyMap()
    }
}

@Composable
fun StationboardList(navController: NavHostController,
                     modifier: Modifier = Modifier,
                     model: AppViewModel = viewModel()/*prende un istanza del mio model*/) {
    Column(modifier = modifier) {
        Box {
            Row { //DROPDOWN!!
                Text(model.station, fontSize = 24.sp, modifier = Modifier.clickable {
                    //show the dropdown when the text is clicked
                    model.showDropdown.value = true
                })
                Icon(painter = painterResource(
                    id = R.drawable.baseline_arrow_drop_down_24),
                    contentDescription = "Arrow down")
            }
            DropdownMenu(expanded = model.showDropdown.value,
                onDismissRequest = {
                    model.showDropdown.value = false
                }) {
                model.stations.forEach {
                    DropdownMenuItem(
                        {
                            Text(it, fontSize = 24.sp)
                        }, onClick = { //ascoltatore dropdown
                            model.station = it /*it = nome stazione zurich o wintertur! */
                            model.openServer()
                            model.showDropdown.value = false
                        })
                }
            }
        }

        //componenti che ci permettono di non andare a caricare subuto tutti i dati ma di caricarne solo un numero poi dinamicamente aggiungerli mentre scrolliamo!
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(model.entries) {index, entry -> //per itemsIndexed passare indicedella lista! come index
                Column(modifier = Modifier.clickable {   //questo e ascoltatoatore su lista! che chiama una route del composable definita nell onCreate
                    navController.navigate("barzigola/${index}")
                }) {
                    Row {
                        Text(entry.dateTime, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f))

                        Text(entry.train, fontSize = 24.sp,
                            modifier = Modifier.weight(1f), textAlign = TextAlign.Center)

                        Text(entry.stop.platform, fontSize = 24.sp,
                            modifier = Modifier.weight(1f), textAlign = TextAlign.Right)
                    }
                    Text(entry.to, fontSize = 24.sp, fontStyle = FontStyle.Italic)
                }

            }
        }

    }
}