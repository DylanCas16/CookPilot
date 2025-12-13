package com.example.cookpilot.viewmodel

import com.example.cookpilot.repository.HistoryRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cookpilot.data.AppContainer
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { HistoryViewModel(container.historyRepository) }
            }
    }

    private val _historyRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val historyRecipes: StateFlow<List<Recipe>> = _historyRecipes.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadUserHistory(userId: String) {
        viewModelScope.launch {
            val result = historyRepository.getUserHistory(userId)
            when (result) {
                is UiState.Success -> {
                    _historyRecipes.value = result.data
                    _error.value = null
                }
                is UiState.Error -> {
                    _error.value = result.message
                }

                UiState.Idle -> {}
                UiState.Loading -> {}
            }
        }
    }

    fun saveRecipeView(userId: String, recipeId: String) {
        viewModelScope.launch {
            historyRepository.saveRecipeView(userId, recipeId)
        }
    }

    fun clearHistory(userId: String) {
        viewModelScope.launch {
            historyRepository.clearUserHistory(userId)
            _historyRecipes.value = emptyList()
        }
    }
}
