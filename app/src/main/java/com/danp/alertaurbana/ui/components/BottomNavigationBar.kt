package com.danp.alertaurbana.ui.components

// ui/components/BottomNavigationBar.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.danp.alertaurbana.ui.navegation.BottomNavItems

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        BottomNavItems.items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } == true

            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount != null && item.badgeCount > 0) {
                                Badge {
                                    Text(
                                        text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            } else if (item.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // Evitar múltiples copias de la misma pantalla
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}