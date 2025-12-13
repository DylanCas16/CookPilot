package com.example.cookpilot.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cookpilot.data.AppContainer
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { RecipeViewModel(container.recipeRepository) }
            }
    }

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _userRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val userRecipes: StateFlow<List<Recipe>> = _userRecipes.asStateFlow()

    private val _loadAllRecipesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Idle)
    val loadAllRecipesState: StateFlow<UiState<List<Recipe>>> = _loadAllRecipesState.asStateFlow()

    private val _loadUserRecipesState = MutableStateFlow<UiState<List<Recipe>>>(UiState.Idle)
    val loadUserRecipesState: StateFlow<UiState<List<Recipe>>> = _loadUserRecipesState.asStateFlow()

    fun loadAllRecipes() {
        viewModelScope.launch {
            _loadAllRecipesState.value = UiState.Loading
            try {
                val recipes = repository.getAllRecipes()
                _recipes.value = recipes
                _loadAllRecipesState.value = UiState.Success(recipes)
            } catch (e: Exception) {
                val errorType = determineErrorType(e)
                val errorMessage = getErrorMessage(e, errorType)
                _loadAllRecipesState.value = UiState.Error(errorMessage, errorType)
                e.printStackTrace()
            }
        }
    }

    fun loadUserRecipes(userId: String) {
        viewModelScope.launch {
            _loadUserRecipesState.value = UiState.Loading
            try {
                val userRecipes = repository.getRecipesByCreator(userId)
                _userRecipes.value = userRecipes
                _loadUserRecipesState.value = UiState.Success(userRecipes)
            } catch (e: Exception) {
                val errorType = determineErrorType(e)
                val errorMessage = getErrorMessage(e, errorType)
                _loadUserRecipesState.value = UiState.Error(errorMessage, errorType)
                e.printStackTrace()
            }
        }
    }

    fun clearLoadAllRecipesState() { _loadAllRecipesState.value = UiState.Idle }
    fun clearLoadUserRecipesState() { _loadUserRecipesState.value = UiState.Idle }

    fun createRecipeFromForm(
        title: String,
        description: String,
        steps: String,
        difficulty: Int,
        ingredients: List<String>,
        cookingTime: Int,
        creator: String,
        dietaryTags: List<String>,
        fileUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.createRecipeFromForm(
                    title, description, steps, difficulty,
                    ingredients, cookingTime, creator, dietaryTags, fileUri
                )
                _recipes.value = repository.getAllRecipes()
                loadUserRecipes(creator)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                val errorType = determineErrorType(e)
                val errorMessage = getErrorMessage(e, errorType)
                onError(errorMessage)
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
        dietaryTags: List<String>,
        newImageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateRecipe(
                    recipeId, title, description, steps,
                    difficulty, ingredients, cookingTime, dietaryTags, newImageUri
                )
                _recipes.value = repository.getAllRecipes()
                loadUserRecipes(creator)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                val errorType = determineErrorType(e)
                val errorMessage = getErrorMessage(e, errorType, "updating")
                onError(errorMessage)
            }
        }
    }

    fun deleteRecipe(
        recipeId: String,
        creator: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val success = repository.deleteRecipe(recipeId)
                if (success) {
                    _recipes.value = repository.getAllRecipes()
                    loadUserRecipes(creator)
                    onSuccess()
                } else {
                    onError("Failed to delete recipe.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val errorType = determineErrorType(e)
                val errorMessage = getErrorMessage(e, errorType, "deleting")
                onError(errorMessage)
            }
        }
    }

    private fun determineErrorType(exception: Exception): ErrorType {
        return when {
            exception is java.net.UnknownHostException ||
                    exception is java.net.SocketTimeoutException ||
                    exception is java.io.IOException -> ErrorType.NETWORK
            exception.message?.contains("401", ignoreCase = true) == true ||
                    exception.message?.contains("unauthorized", ignoreCase = true) == true -> ErrorType.AUTHENTICATION
            exception.message?.contains("500", ignoreCase = true) == true ||
                    exception.message?.contains("503", ignoreCase = true) == true -> ErrorType.SERVER
            else -> ErrorType.GENERIC
        }
    }

    private fun getErrorMessage(
        exception: Exception,
        errorType: ErrorType,
        operation: String = "performing operation"
    ): String {
        return when (errorType) {
            ErrorType.NETWORK -> "Unable to connect to the database. Check your internet connection."
            ErrorType.SERVER -> "Server is unavailable. Please try again later."
            ErrorType.AUTHENTICATION -> "Authentication error. Please log in again."
            ErrorType.GENERIC -> exception.message ?: "Unknown error while $operation."
        }
    }
}
