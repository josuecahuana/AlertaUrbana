package com.danp.alertaurbana.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.danp.alertaurbana.ui.navegation.NavigationRoutes

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(NavigationRoutes.LIST, "Reportes", Icons.Default.List),
    BottomNavItem(NavigationRoutes.CREATE_REPORT, "Registrar", Icons.Default.Add),
    BottomNavItem(NavigationRoutes.MAP, "Mapa", Icons.Default.LocationOn),
    BottomNavItem(NavigationRoutes.PROFILE, "Perfil", Icons.Default.Person)
)
