package com.example.cookpilot.viewmodel

import HistoryRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookpilot.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    application: Application,
    private val historyRepository: HistoryRepository = HistoryRepository()
) : AndroidViewModel(application) {
    private val _historyRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val historyRecipes: StateFlow<List<Recipe>> = _historyRecipes.asStateFlow()

    fun loadUserHistory(userId: String) {
        viewModelScope.launch {
            val recipes = historyRepository.getUserHistory(userId)
            _historyRecipes.value = recipes
        }
    }

    fun saveRecipeView(userId: String, recipeId: String) {
        viewModelScope.launch {
            historyRepository.saveRecipeView(userId, recipeId)
        }
    }
}
