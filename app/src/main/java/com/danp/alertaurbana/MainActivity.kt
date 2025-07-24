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

                    LaunchedEffect(Unit) {
                        authViewModel.checkLogin()
                        UserSyncWorker.schedule(applicationContext)
                    }

                    AppNavigation(navController = navController, authState = authState)
                }
            }
        }
    }
}
