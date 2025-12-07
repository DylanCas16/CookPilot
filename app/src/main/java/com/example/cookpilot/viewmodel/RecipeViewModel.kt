package com.example.cookpilot.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository(application)

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _userRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val userRecipes: StateFlow<List<Recipe>> = _userRecipes

    fun loadAllRecipes() {
        viewModelScope.launch {
            val recipes = repository.getAllRecipes()
            _recipes.value = recipes
        }
    }

    fun loadUserRecipes(userId: String) {
        viewModelScope.launch {
            val userRecipes = repository.getRecipesByCreator(userId)
            _userRecipes.value = userRecipes
        }
    }

    fun createRecipeFromForm(
        title: String,
        description: String,
        steps: String,
        difficulty: Int,
        ingredients: List<String>,
        cookingTime: Int,
        creator: String,
        fileUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                repository.createRecipeFromForm(
                    title, description, steps, difficulty,
                    ingredients, cookingTime, creator, fileUri
                )

                _recipes.value = repository.getAllRecipes()
                loadUserRecipes(creator)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateRecipe(
        recipeId: String,
        title: String,
        description: String,
        steps: String,
        difficulty: Int,
        ingredients: List<String>,
        cookingTime: Int,
        creator: String,
        newImageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                println("🔵 Updating recipe from ViewModel")
                repository.updateRecipe(
                    recipeId, title, description, steps,
                    difficulty, ingredients, cookingTime, newImageUri
                )

                // Recargar listas
                _recipes.value = repository.getAllRecipes()
                loadUserRecipes(creator)

                println("✅ Recipe updated and lists refreshed")
            } catch (e: Exception) {
                println("❌ Error in updateRecipe: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun deleteRecipe(recipeId: String, creator: String) {
        viewModelScope.launch {
            try {
                println("🔵 Deleting recipe from ViewModel")
                val success = repository.deleteRecipe(recipeId)

                if (success) {
                    // Recargar listas
                    _recipes.value = repository.getAllRecipes()
                    loadUserRecipes(creator)
                    println("✅ Recipe deleted and lists refreshed")
                } else {
                    println("❌ Failed to delete recipe")
                }
            } catch (e: Exception) {
                println("❌ Error in deleteRecipe: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
