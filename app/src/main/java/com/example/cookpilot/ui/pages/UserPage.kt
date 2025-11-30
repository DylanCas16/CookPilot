//package com.example.cookpilot.ui.pages
//
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.PickVisualMediaRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.example.cookpilot.model.Recipe
//import com.example.cookpilot.ui.components.RecipeList // <--- Importamos el componente de lista
//
//@Composable
//fun UserPage() {
//    // --- ESTADOS ---
//    var userName by remember { mutableStateOf("Chef CookPilot") }
//    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
//
//    // 1. Declaramos la lista de recetas como MutableStateList
//    val recetas = remember { mutableStateListOf(
//        Recipe("Pasta Carbonara", "Clásica italiana", "...", 3, listOf("Pasta", "Huevo"), 20, "Chef", null),
//        Recipe("Sushi Roll", "Fresco y delicioso", "...", 4, listOf("Arroz", "Salmón"), 45, "Chef", null),
//        Recipe("Tarta de Queso", "Postre suave", "...", 2, listOf("Queso", "Galleta"), 60, "Chef", null)
//    )}
//
//    // Estado para controlar qué receta se ha seleccionado para editar/borrar
//    var recetaSeleccionada by remember { mutableStateOf<Recipe?>(null) }
//    var showActionDialog by remember { mutableStateOf(false) }
//
//    // Launcher para cambiar foto de perfil
//    val photoPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia(),
//        onResult = { uri -> profileImageUri = uri }
//    )
//
//    // --- INTERFAZ PRINCIPAL ---
//    Scaffold { paddingValues ->
//        // Usamos LazyColumn para el Layout principal (Cabecera + Lista)
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp), // Padding horizontal para todo el contenido
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // 1. SECCIÓN DE CABECERA (Nombre y Foto)
//            item {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
//                ) {
//                    // Nombre del Usuario
//                    Text(
//                        text = userName,
//                        style = MaterialTheme.typography.headlineMedium,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//
//                    // Foto de Perfil (Clickable para cambiar)
//                    Box(
//                        modifier = Modifier
//                            .size(120.dp)
//                            .clip(CircleShape)
//                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
//                            .background(Color.LightGray)
//                            .clickable {
//                                photoPickerLauncher.launch(
//                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                                )
//                            },
//                        contentAlignment = Alignment.Center
//                    ) {
//                        if (profileImageUri != null) {
//                            Icon(
//                                painter = painterResource(android.R.drawable.ic_menu_gallery),
//                                contentDescription = null,
//                                modifier = Modifier.size(50.dp),
//                                tint = Color.DarkGray
//                            )
//                        } else {
//                            Icon(
//                                imageVector = Icons.Default.Person,
//                                contentDescription = "Foto por defecto",
//                                modifier = Modifier.size(60.dp),
//                                tint = Color.White
//                            )
//                        }
//                    }
//                    Text(
//                        text = "Change profile picture",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//                }
//
//                HorizontalDivider()
//
//                // Título de la sección de recetas
//                Text(
//                    text = "My Recipes (${recetas.size})",
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier
//                       .fillMaxWidth()
//                        .padding(vertical = 16.dp)
//                )
//            }
//
//            // 2. LISTA DE RECETAS (Usando el componente RecipeList)
//            // Usamos un 'item' para contener el RecipeList si queremos que la cabecera
//            // siga haciendo scroll, pero LazyColumn funciona mejor si usamos 'items'
//
//            // Para integrar RecipeList en LazyColumn, NO podemos poner RecipeList directamente
//            // porque ya contiene otro LazyColumn. Debemos usar la lógica de items del padre.
//
//            // Opción 1: Reemplazar RecipeList por la lógica de items aquí (Más sencillo para la estructura actual)
//            // items(recetas) { receta ->
//            //     RecipeItemCard(
//            //         recipe = receta,
//            //         onClick = {
//            //             recetaSeleccionada = receta
//            //             showActionDialog = true
//            //         }
//            //     )
//            //     Spacer(modifier = Modifier.height(12.dp))
//            // }
//
//            // Opción 2: Usar el componente RecipeList y permitirle manejar su propio LazyColumn
//            // Si usamos LazyColumn dentro de LazyColumn, debemos darle un tamaño fijo.
//            // Para mantener la reusabilidad y simplicidad, vamos a usar el enfoque de items aquí,
//            // ya que UserPage tiene una estructura única (cabecera + lista).
//
//            items(recetas) { receta ->
//                RecipeItemCard(
//                    recipe = receta,
//                    onClick = {
//                        recetaSeleccionada = receta
//                        showActionDialog = true
//                    }
//                )
//                // NO ponemos Spacer aquí, se deja en RecipeItemCard si es necesario
//            }
//        }
//    }
//
//    // --- DIÁLOGO DE ACCIONES (Editar / Eliminar) ---
//    if (showActionDialog && recetaSeleccionada != null) {
//        AlertDialog(
//            onDismissRequest = { showActionDialog = false },
//            icon = { Icon(Icons.Default.Edit, contentDescription = null) },
//            title = { Text(text = "Gestionar Receta") },
//            text = {
//                Text("¿Qué deseas hacer con '${recetaSeleccionada?.recipeName}'?")
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        // Lógica de EDITAR (Navegar a pantalla de edición)
//                        println("Editando ${recetaSeleccionada?.recipeName}")
//                        showActionDialog = false
//                    }
//                ) {
//                    Text("Editar")
//                }
//            },
//            dismissButton = {
//                OutlinedButton(
//                    onClick = {
//                        // Lógica de ELIMINAR
//                        // 2. Eliminamos de la lista mutable
//                        recetas.remove(recetaSeleccionada)
//                        showActionDialog = false
//                    },
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = MaterialTheme.colorScheme.error
//                    )
//                ) {
//                    Text("Eliminar")
//                }
//            }
//        )
//    }
//}
//
//// --- COMPONENTE PARA CADA ITEM DE LA LISTA (Mantenido aquí por simplicidad) ---
//@Composable
//fun RecipeItemCard(
//    recipe: Recipe,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() } // Al hacer clic abrimos el diálogo
//            .padding(bottom = 12.dp), // Añadimos el separador aquí
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = recipe.title,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = "Dificultad: ${recipe.difficulty}/5 • ${recipe.cookingTime} min",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray
//                )
//            }
//            // Icono indicativo de que se puede interactuar
//            Icon(
//                painter = painterResource(android.R.drawable.ic_menu_more),
//                contentDescription = "Opciones",
//                tint = Color.Gray
//            )
//        }
//    }
//}