//package com.example.cookpilot.ui.components
//
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.cookpilot.model.Recipe
//import com.example.cookpilot.ui.pages.RecipeItemCard
//
//
//@Composable
//fun RecipeList(
//    // Parámetros para reusabilidad:
//    recipes: List<Recipe>,
//    onRecipeClick: (Recipe) -> Unit, // Acción a ejecutar al hacer clic
//    modifier: Modifier = Modifier
//) {
//    // 1. Usamos LazyColumn como contenedor principal
//    LazyColumn(
//        modifier = modifier.fillMaxWidth()
//    ) {
//        // Puedes poner un header si lo necesitas, pero lo hago opcional para reusabilidad
//        if (recipes.isEmpty()) {
//            item {
//                Text(
//                    text = "No se encontraron recetas.",
//                    modifier = Modifier.padding(16.dp),
//                    style = MaterialTheme.typography.bodyLarge
//                )
//            }
//        }
//
//        // 2. Usamos la función items() para renderizar la lista
//        items(recipes) { receta ->
//            // RecipeItemCard debe estar definido en tu proyecto
//            RecipeItemCard(
//                recipe = receta,
//                // 3. Al hacer click, ejecutamos la función pasada por el padre
//                onClick = { onRecipeClick(receta) }
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//        }
//    }
//}