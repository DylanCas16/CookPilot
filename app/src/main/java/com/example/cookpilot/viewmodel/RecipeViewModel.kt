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

    fun createRecipeFromForm(
        title: String,
        description: String,
        steps: String,
        difficulty: Int,
        ingredients: List<String>,
        cookingTime: Int,
        creator: String,
        fileId: Uri?
    ) {
        viewModelScope.launch {
            repository.createRecipeFromForm(
                title,
                description,
                steps,
                difficulty,
                ingredients,
                cookingTime,
                creator,
                fileId
            )
            _recipes.value = repository.getAllRecipes()
        }
    }
}
