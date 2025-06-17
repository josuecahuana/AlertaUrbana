package com.danp.alertaurbana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danp.alertaurbana.ui.view.CreateReportScreen
import com.danp.alertaurbana.ui.components.MainLayout
import com.danp.alertaurbana.ui.navegation.NavigationRoutes
import com.danp.alertaurbana.ui.theme.AlertaUrbanaTheme
import com.danp.alertaurbana.ui.view.*
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
                    HurtosApp()
                }
            }
        }
    }
}

@Composable
fun HurtosApp() {
    val navController = rememberNavController()
    val startDestination: String = NavigationRoutes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate(NavigationRoutes.LIST) {
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(NavigationRoutes.LIST) {
                        popUpTo(NavigationRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        /*
        composable("reports_list") {
            ReportsListScreen(
                navController = navController
            )
        }

        composable("report_detail/{reportId}") { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ReportDetailScreen(
                reportId = reportId,
                navController = navController,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }*/

        // Pantallas con bottom navigation
        composable(NavigationRoutes.LIST) {
            MainLayout(
                navController = navController,
                title = "Lista de Reportes",
                showBottomBar = true,
                showBackButton = false,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            // Navegar a la pantalla de crear reporte
                            navController.navigate(NavigationRoutes.CREATE_REPORT)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Crear Reporte"
                        )
                    }
                }
            ) { paddingValues ->
                ReportsListScreen(
                    onNavigateToDetail = { reportId ->
                        navController.navigate(NavigationRoutes.detailWithId(reportId))
                    },
                    modifier = Modifier.padding(paddingValues),
                    navController = navController
                )
            }
        }

        composable(NavigationRoutes.CREATE_REPORT) {
            CreateReportScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationRoutes.DETAIL) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""

            MainLayout(
                navController = navController,
                title = "Detalle del Reporte",
                showBottomBar = true,
                showBackButton = true
            ) { paddingValues ->
                ReportDetailScreen(
                    reportId = reportId,
                    navController = navController,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable(NavigationRoutes.MAP) {
            MainLayout(
                navController = navController,
                title = "Mapa de Reportes",
                showBottomBar = true
            ) { paddingValues ->
                MapScreen(
                    onNavigateToDetail = { reportId ->
                        navController.navigate(NavigationRoutes.detailWithId(reportId))
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable(NavigationRoutes.PROFILE) {
            MainLayout(
                navController = navController,
                title = "Mi Perfil",
                showBottomBar = true
            ) { /*paddingValues ->
                ProfileScreen(
                    onLogout = {
                        navController.navigate(NavigationRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )*/
            }
        }
    }
}
