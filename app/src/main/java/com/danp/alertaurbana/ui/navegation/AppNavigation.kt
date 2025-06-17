package com.danp.alertaurbana.ui.navegation

// navigation/AppNavigation.kt
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danp.alertaurbana.ui.view.LoginScreen
import com.danp.alertaurbana.ui.view.RegisterScreen


//Esta Funcion debería ejecutarse en vez de la funcion que está en el MAinActivity
//Por mientras este archivo no interviene en la app
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationRoutes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantallas sin bottom navigation
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

        // Pantallas con bottom navigation

        /*

        *******************************************************************
        Claude me generó este archivo para la navegación, pero como la navegación ya se estaba
        en el Main Activity el contenido de este archivo se editó en el Main activity
        *******************************************************************

         |  |  |  |  |  |  |  |  |  |
         V  V  V  V  V  V  V  V  V  V

        composable(NavigationRoutes.REPORT_LIST) {
            MainLayout(
                navController = navController,
                title = "Lista de Reportes",
                showBottomBar = true,
                showBackButton = false
            ) { paddingValues ->
                ReportListScreen(
                    onNavigateToDetail = { reportId ->
                        navController.navigate(NavigationRoutes.reportDetail(reportId))
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable(NavigationRoutes.REPORT_DETAIL) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""

            MainLayout(
                navController = navController,
                title = "Detalle del Reporte",
                showBottomBar = true,
                showBackButton = true
            ) { paddingValues ->
                ReportDetailScreen(
                    reportId = reportId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable(NavigationRoutes.CREATE_REPORT) {
            MainLayout(
                navController = navController,
                title = "Crear Reporte",
                showBottomBar = true,
                showBackButton = true
            ) { paddingValues ->
                CreateReportScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onReportCreated = {
                        navController.navigate(NavigationRoutes.REPORT_LIST) {
                            popUpTo(NavigationRoutes.CREATE_REPORT) { inclusive = true }
                        }
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
                        navController.navigate(NavigationRoutes.reportDetail(reportId))
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
            ) { paddingValues ->
                ProfileScreen(
                    onLogout = {
                        navController.navigate(NavigationRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }*/
    }
}