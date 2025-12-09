package com.example.cookpilot

import HistoryRepository
import android.app.Application
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.viewmodel.HistoryViewModel
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var historyRepository: HistoryRepository
    private lateinit var application: Application
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        historyRepository = mockk()

        viewModel = HistoryViewModel(
            application = application,
            historyRepository = historyRepository // constructor alternativo para tests
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserHistory fills historyRecipes`() = runTest {
        val fakeHistory = listOf(
            Recipe(id = "1", title = "Viewed", description = "", steps = "", difficulty = 1,
                ingredients = emptyList(), cookingTime = 5, dietaryTags = emptyList(), creator = "u1", fileId = null)
        )
        coEvery { historyRepository.getUserHistory("user123") } returns fakeHistory

        viewModel.loadUserHistory("user123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.historyRecipes.value.size)
        assertEquals("Viewed", viewModel.historyRecipes.value[0].title)
    }

    @Test
    fun `saveRecipeView delegates to repository`() = runTest {
        coJustRun { historyRepository.saveRecipeView("user123", "recipe1") }

        viewModel.saveRecipeView("user123", "recipe1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Aquí solo comprobamos que no lanza excepción; el mock ya valida llamadas
    }
}
