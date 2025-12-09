package com.example.cookpilot

import android.app.Application
import android.net.Uri
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.repository.RecipeRepository
import com.example.cookpilot.viewmodel.RecipeViewModel
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: RecipeRepository
    private lateinit var application: Application
    private lateinit var viewModel: RecipeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        repository = mockk()

        viewModel = RecipeViewModel(
            application = application,
            repository = repository // constructor alternativo para tests
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAllRecipes updates recipes state`() = runTest {
        // given
        val fakeRecipes = listOf(
            Recipe(id = "1", title = "Pasta", description = "", steps = "", difficulty = 2,
                ingredients = listOf("Pasta"), cookingTime = 10, dietaryTags = emptyList(), creator = "u1", fileId = null),
            Recipe(id = "2", title = "Salad", description = "", steps = "", difficulty = 1,
                ingredients = listOf("Lettuce"), cookingTime = 5, dietaryTags = emptyList(), creator = "u1", fileId = null),
        )
        coEvery { repository.getAllRecipes() } returns fakeRecipes

        // when
        viewModel.loadAllRecipes()
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(2, viewModel.recipes.value.size)
        assertEquals("Pasta", viewModel.recipes.value[0].title)
    }

    @Test
    fun `loadUserRecipes loads only user recipes`() = runTest {
        val fakeUserRecipes = listOf(
            Recipe(id = "1", title = "UserRecipe", description = "", steps = "", difficulty = 3,
                ingredients = emptyList(), cookingTime = 20, dietaryTags = emptyList(), creator = "user123", fileId = null)
        )
        coEvery { repository.getRecipesByCreator("user123") } returns fakeUserRecipes

        viewModel.loadUserRecipes("user123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.userRecipes.value.size)
        assertEquals("user123", viewModel.userRecipes.value[0].creator)
    }

    @Test
    fun `createRecipeFromForm refreshes recipes and userRecipes`() = runTest {
        // given
        coJustRun {
            repository.createRecipeFromForm(
                title = any(),
                description = any(),
                steps = any(),
                difficulty = any(),
                ingredients = any(),
                cookingTime = any(),
                creator = any(),
                dietaryTags = any(),
                fileUri = any()
            )
        }

        val afterCreateAll = listOf(
            Recipe(id = "1", title = "New Recipe", description = "", steps = "", difficulty = 2,
                ingredients = emptyList(), cookingTime = 10, dietaryTags = emptyList(), creator = "user123", fileId = null)
        )
        coEvery { repository.getAllRecipes() } returns afterCreateAll
        coEvery { repository.getRecipesByCreator("user123") } returns afterCreateAll

        // when
        viewModel.createRecipeFromForm(
            title = "New Recipe",
            description = "desc",
            steps = "steps",
            difficulty = 2,
            ingredients = listOf("ing"),
            cookingTime = 10,
            creator = "user123",
            dietaryTags = emptyList(),
            fileUri = null as Uri?
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(1, viewModel.recipes.value.size)
        assertEquals(1, viewModel.userRecipes.value.size)
        assertEquals("New Recipe", viewModel.recipes.value[0].title)
    }

    @Test
    fun `deleteRecipe refreshes lists when success`() = runTest {
        // given
        coEvery { repository.deleteRecipe("1") } returns true
        coEvery { repository.getAllRecipes() } returns emptyList()
        coEvery { repository.getRecipesByCreator("user123") } returns emptyList()

        // when
        viewModel.deleteRecipe("1", creator = "user123")
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        assertEquals(0, viewModel.recipes.value.size)
        assertEquals(0, viewModel.userRecipes.value.size)
    }
}
