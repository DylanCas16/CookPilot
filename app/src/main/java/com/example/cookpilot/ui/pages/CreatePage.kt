package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cookpilot.ui.components.RecipeForm

@Composable
fun CreatePage() {
    // Añadimos un Column con scroll para que el formulario quepa en pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Habilita el scroll vertical
    ) {
        RecipeForm(
            onSaveRecipe = { datos ->
                // Aquí recibes los datos cuando pulsas el botón
                println("Guardando receta: ${datos.titulo}")
                println("Ingredientes: ${datos.ingredientes}")
            }
        )
    }
}