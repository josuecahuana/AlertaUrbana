package com.danp.alertaurbana.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.ExitToApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val userReports by viewModel.userReports.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
        viewModel.loadUserReports()
    }

    when (val state = uiState) {
        is UserUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UserUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(state.message)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.createDefaultProfile() }) {
                    Text("Crear Perfil")
                }
            }
        }

        is UserUiState.Success -> {
            val user = state.user
            var isEditing by remember { mutableStateOf(false) }

            var nombre by remember { mutableStateOf(TextFieldValue(user.nombre ?: "")) }
            var direccion by remember { mutableStateOf(TextFieldValue(user.direccion ?: "")) }
            var telefono by remember { mutableStateOf(TextFieldValue(user.telefono ?: "")) }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                authViewModel.logout()
                                navController.navigate(NavigationRoutes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = Color.Red
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Cerrar sesión", color = Color.Red)
                        }
                    }
                }
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

                        Text("Correo: ${state.email}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))

                        if (isEditing) {
                            EditableField("Nombre", nombre) { nombre = it }
                            EditableField("Dirección", direccion) { direccion = it }
                            EditableField("Teléfono", telefono, KeyboardType.Phone) { telefono = it }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.updateProfile(nombre.text, direccion.text, telefono.text)
                                        isEditing = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Guardar cambios")
                                }

                                OutlinedButton(
                                    onClick = {
                                        // Restaurar valores originales y cancelar edición
                                        nombre = TextFieldValue(user.nombre ?: "")
                                        direccion = TextFieldValue(user.direccion ?: "")
                                        telefono = TextFieldValue(user.telefono ?: "")
                                        isEditing = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        } else {
                            ReadOnlyField("Nombre", nombre.text)
                            ReadOnlyField("Dirección", direccion.text)
                            ReadOnlyField("Teléfono", telefono.text)

                            Spacer(Modifier.height(12.dp))

                            Button(onClick = { isEditing = true }) {
                                Text("Editar perfil")
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text("Tus reportes:", style = MaterialTheme.typography.titleMedium)

                        if (userReports.isEmpty()) {
                            Text("No tienes reportes aún.")
                        }
                    }
                }

                items(userReports.size) { index ->
                    val report = userReports[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
                            .background(Color(0xFFDBF1EC), shape = MaterialTheme.shapes.medium)
                            .padding(8.dp)

                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(report.images ?: ""),
                            contentDescription = "Imagen del reporte",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = report.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = report.description ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                color = Color.Gray
                            )
                            report.date?.let { timestamp ->
                                Text(
                                    text = "Fecha: ${formatDate(timestamp)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(32.dp))
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
        }

        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando perfil...")
            }
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
                .padding(top = 4.dp)
                .background(Color(0xFFECDFCC), shape = MaterialTheme.shapes.medium)
                .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
                .padding(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}
fun formatDate(timestamp: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(timestamp)
}
@Composable
fun ReadOnlyField(label: String, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Text(
            text = value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .background(Color(0xFFECDFCC), shape = MaterialTheme.shapes.medium)
                .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
                .padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
