// ui/navegation/AppNavigation.kt
package com.danp.alertaurbana.ui.navegation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.danp.alertaurbana.ui.components.MainLayout
import com.danp.alertaurbana.ui.view.*
import com.danp.alertaurbana.ui.viewmodel.AuthState
import com.danp.alertaurbana.ui.viewmodel.AuthViewModel
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

@Composable
fun AppNavigation(
    navController: NavHostController,
    authState: AuthState
) {
    val startDestination = when (authState) {
        is AuthState.LoginSuccess -> NavigationRoutes.LIST
        else -> NavigationRoutes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationRoutes.LIST) {
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavigationRoutes.REGISTER)
                }
            )
        }

        composable(NavigationRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavigationRoutes.LIST) {
                        popUpTo(NavigationRoutes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(NavigationRoutes.LIST) {
            MainLayout(
                navController = navController,
                showBottomBar = true,
                title = "Reportes"
            ) { padding ->
                ReportsListScreen(navController = navController, modifier = Modifier.padding(padding))
            }
        }

        composable(NavigationRoutes.CREATE_REPORT) {
            MainLayout(
                navController = navController,
                showBottomBar = true,
                showBackButton = true,
                title = "Registrar Reporte"
            ) { padding ->
                CreateReportScreen(
                    modifier = Modifier.padding(padding),
                    onNavigateBack = { navController.popBackStack() } // ← Esta línea es clave
                )
            }
        }

        composable(NavigationRoutes.MAP) {
            MainLayout(
                navController = navController,
                showBottomBar = true,
                title = "Mapa de Calor"
            ) { padding ->
                HeatMapScreen(modifier = Modifier.padding(padding))
            }
        }

        composable(NavigationRoutes.PROFILE) {
            val authViewModel: AuthViewModel = hiltViewModel()
            MainLayout(
                navController = navController,
                showBottomBar = true,
                title = "Perfil"
            ) { padding ->
                ProfileScreen(
                    authViewModel = authViewModel,
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable("${NavigationRoutes.DETAIL}/{reportId}") { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            MainLayout(
                navController = navController,
                showBottomBar = false,
                showBackButton = true,
                title = "Detalle del Reporte"
            ) { padding ->
                ReportDetailScreen(
                    reportId = reportId,
                    navController = navController,
                    modifier = Modifier.padding(padding),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
