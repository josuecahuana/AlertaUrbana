package com.danp.alertaurbana.ui.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.danp.alertaurbana.ui.view.*
/*
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.LOGIN
    ) {
        composable(NavigationRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(NavigationRoutes.LIST) },
                onRegisterClick = { navController.navigate(NavigationRoutes.REGISTER) }
            )
        }

        composable(NavigationRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(NavigationRoutes.LOGIN) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
*/