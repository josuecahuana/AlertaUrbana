package com.danp.alertaurbana.ui.navegation

// navigation/BottomNavItem.kt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val hasNews: Boolean = false,
    val badgeCount: Int? = null
)

object BottomNavItems {
    val items = listOf(
        BottomNavItem(
            route = NavigationRoutes.LIST,
            title = "Reportes",
            icon = Icons.Default.List
        ),
        BottomNavItem(
            route = NavigationRoutes.MAP,
            title = "Mapa",
            icon = Icons.Default.LocationOn
        ),
        BottomNavItem(
            route = NavigationRoutes.PROFILE,
            title = "Perfil",
            icon = Icons.Default.Person
        )
    )
}