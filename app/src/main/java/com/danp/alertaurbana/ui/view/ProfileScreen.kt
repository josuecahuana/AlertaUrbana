package com.danp.alertaurbana.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.danp.alertaurbana.ui.navegation.NavigationRoutes
import com.danp.alertaurbana.ui.viewmodel.AuthViewModel
import com.danp.alertaurbana.ui.viewmodel.ProfileViewModel
import com.danp.alertaurbana.ui.viewmodel.ProfileViewModel.UserUiState

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (val state = uiState) {
            is UserUiState.Loading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is UserUiState.Error -> {
                Text(state.message)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.createDefaultProfile() }) {
                    Text("Crear Perfil")
                }
            }

            is UserUiState.Success -> {
                val user = state.user

                var nombre by remember { mutableStateOf(TextFieldValue(user.nombre ?: "")) }
                var direccion by remember { mutableStateOf(TextFieldValue(user.direccion ?: "")) }
                var telefono by remember { mutableStateOf(TextFieldValue(user.telefono ?: "")) }

                if (!user.fotoUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = user.fotoUrl),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text("Correo: ${state.email}", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(16.dp))

                EditableField(label = "Nombre", value = nombre) { nombre = it }
                EditableField(label = "Dirección", value = direccion) { direccion = it }
                EditableField(
                    label = "Teléfono",
                    value = telefono,
                    keyboardType = KeyboardType.Phone
                ) { telefono = it }

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    viewModel.updateProfile(nombre.text, direccion.text, telefono.text)
                }) {
                    Text("Guardar cambios")
                }

                Spacer(Modifier.height(24.dp))
                Text("Tus reportes:", style = MaterialTheme.typography.titleMedium)
                Text("(Aquí irán los reportes asociados al usuario)")
            }

            else -> {
                Text("Cargando perfil...")
            }
        }

        Spacer(Modifier.height(32.dp))

        // 🔴 Botón de Logout visible en cualquier estado
        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate(NavigationRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Cerrar sesión", color = Color.White)
        }
    }
}


@Composable
fun EditableField(
    label: String,
    value: TextFieldValue,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (TextFieldValue) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .border(1.dp, Color.Gray, MaterialTheme.shapes.medium)
                .padding(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}
