package com.danp.alertaurbana.ui.components

// ui/components/MainLayout.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    navController: NavController,
    title: String,
    showBottomBar: Boolean = true,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        /*topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = { onBackClick?.invoke() ?: navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },*/
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}