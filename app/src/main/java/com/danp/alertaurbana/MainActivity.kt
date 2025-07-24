// MainActivity.kt
package com.danp.alertaurbana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.danp.alertaurbana.ui.navegation.AppNavigation
import com.danp.alertaurbana.ui.theme.AlertaUrbanaTheme
import com.danp.alertaurbana.ui.viewmodel.AuthViewModel
import com.danp.alertaurbana.data.work.UserSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlertaUrbanaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel = hiltViewModel<AuthViewModel>()
                    val authState by authViewModel.authState.collectAsState()

                    var locationPermissionGranted by remember { mutableStateOf(false) }

                    // Solicitud de permiso
                    val requestPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        locationPermissionGranted = isGranted
                    }

                    LaunchedEffect(Unit) {
                        authViewModel.checkLogin()
                        UserSyncWorker.schedule(applicationContext)

                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        } else {
                            locationPermissionGranted = true
                        }
                    }

                    AppNavigation(navController = navController, authState = authState)
                }
            }
        }
    }
}
