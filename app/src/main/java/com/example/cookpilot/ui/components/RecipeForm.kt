package com.example.cookpilot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// La estructura de datos que manejará el formulario
data class RecipeData(
    val titulo: String,
    val descripcion: String,
    val pasos: String,
    val dificultad: Int,
    val ingredientes: List<String>
)

@Composable
fun RecipeForm(
        /*onSaveRecipe: (RecipeData) -> Unit,*/ // 🎯 DESCOMENTADO: Es esencial para devolver los datos
        modifier: Modifier = Modifier
    ) {
        var titulo by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var pasos by remember { mutableStateOf("") }
        var dificultad by remember { mutableIntStateOf(1) } // Default 1

        val ingredientes = remember { mutableStateListOf("") }
        var nuevoIngrediente by remember { mutableStateOf("") }

        FormularioBase(
            titulo = "Nueva Receta",
            textoBoton = "Crear Receta",
            onEnviarClick = {
                val data = RecipeData(
                    titulo = titulo,
                    descripcion = descripcion,
                    pasos = pasos,
                    dificultad = dificultad,
                    ingredientes = ingredientes.filter { it.isNotBlank() }
                )
                /*onSaveRecipe(data)*/ // 🎯 Llamamos a la función de guardar con los datos
            },
            modifier = modifier
        ) { // <-- Este es el slot 'content' que se rellena
            // 1. Título de Receta (String)
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la Receta") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            // 2. Descripción (Varchar)
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción breve") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth().height(100.dp).padding(vertical = 8.dp)
            )

            // 3. Pasos (Varchar - Multilínea)
            OutlinedTextField(
                value = pasos,
                onValueChange = { pasos = it },
                label = { Text("Instrucciones / Pasos (separar por línea)") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Dificultad (Integer)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dificultad (1-5): ", style = MaterialTheme.typography.bodyLarge)
                Button(
                    onClick = { if (dificultad > 1) dificultad-- },
                    enabled = dificultad > 1,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("-")
                }
                Text(text = dificultad.toString(), style = MaterialTheme.typography.titleLarge)
                Button(
                    onClick = { if (dificultad < 5) dificultad++ },
                    enabled = dificultad < 5,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()

            // 5. Ingredientes (Lista de String - Lógica de Añadir)
            Text(text = "Ingredientes:", style = MaterialTheme.typography.titleMedium)

            // Mostrar la lista de ingredientes actuales
            ingredientes.forEachIndexed { index, item ->
                if (item.isNotBlank()) {
                    Text(
                        text = "${index + 1}. $item",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Campo para añadir nuevo ingrediente
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nuevoIngrediente,
                    onValueChange = { nuevoIngrediente = it },
                    label = { Text("Añadir Ingrediente") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (nuevoIngrediente.isNotBlank()) {
                            ingredientes.add(nuevoIngrediente.trim())
                            nuevoIngrediente = "" // Limpiar campo
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Añadir")
                }
            }
        }
    }

@Composable
fun FormularioBase(
    titulo: String,
    textoBoton: String,
    onEnviarClick: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
}