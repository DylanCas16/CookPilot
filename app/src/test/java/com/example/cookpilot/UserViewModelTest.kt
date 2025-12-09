package com.example.cookpilot

import android.app.Application
import android.net.Uri
import com.example.cookpilot.data.PreferencesManager
import com.example.cookpilot.notifications.NotificationScheduler
import com.example.cookpilot.repository.AuthRepository
import com.example.cookpilot.repository.UserRepository
import com.example.cookpilot.ui.components.RegisterUser
import com.example.cookpilot.viewmodel.UserUiState
import com.example.cookpilot.viewmodel.UserViewModel
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
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

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var application: Application
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var notificationScheduler: NotificationScheduler

    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        application = mockk(relaxed = true)
        authRepository = mockk()
        userRepository = mockk()
        preferencesManager = mockk()
        notificationScheduler = mockk()

        viewModel = UserViewModel(
            application = application,
            authRepository = authRepository,
            userRepository = userRepository,
            preferencesManager = preferencesManager,
            notificationScheduler = notificationScheduler
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------------------------------------------------------------------
    // TEST 1: checkSession cuando hay sesión activa
    // ---------------------------------------------------------------------
    @Test
    fun `checkSession sets logged in when session exists`() = runTest {
        // Mock: hay sesión activa
        every { authRepository.hasActiveSession() } returns true

        // Usuario actual simulado
        val mockUser = mockkm.example.cookpilot.repository.AuthUser>()
        every { mockUser.id } returns "user123"
        every { authRepository.getCurrentUser() } returns mockUser

        // Datos extra del usuario
        every { authRepository.getUserData("user123") } returns mapOf(
            "username" to "Juan",
            "profilePictureId" to "file123"
        )

        // Ejecutar
        viewModel.checkSession()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificar estado
        val state: UserUiState = viewModel.uiState.value
        assertTrue(state.isLoggedIn)
        assertEquals("user123", state.userId)
        assertEquals("Juan", state.userName)
        assertEquals("file123", state.profilePictureId)
        assertEquals(null, state.error)
    }

    // ---------------------------------------------------------------------
    // TEST 2: login correcto
    // ---------------------------------------------------------------------
    @Test
    fun `login success updates uiState correctly`() = runTest {
        // Mock login sin error
        coJustRun { authRepository.loginUser("test@example.com", "secret") }

        val mockUser = mockkm.example.cookpilot.repository.AuthUser>()
        every { mockUser.id } returns "user123"
        every { authRepository.getCurrentUser() } returns mockUser

        every { authRepository.getUserData("user123") } returns mapOf(
            "username" to "Juan",
            "profilePictureId" to "file123"
        )

        // Ejecutar
        viewModel.login("test@example.com", "secret")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificar
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isLoggedIn)
        assertTrue(state.success)
        assertEquals("user123", state.userId)
        assertEquals("Juan", state.userName)
        assertEquals("file123", state.profilePictureId)
        assertEquals(null, state.error)
        assertFalse(state.showLoginDialog)
    }

    // ---------------------------------------------------------------------
    // TEST 3: login con error
    // ---------------------------------------------------------------------
    @Test
    fun `login failure sets error and not logged in`() = runTest {
        // Mock: login lanza excepción
        coEvery { authRepository.loginUser(any(), any()) } throws RuntimeException("Invalid credentials")

        viewModel.login("wrong@example.com", "bad")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isLoggedIn)
        assertFalse(state.success)
        assertEquals("Invalid credentials", state.error)
    }

    // ---------------------------------------------------------------------
    // TEST 4: register correcto llama a login y deja usuario logeado
    // ---------------------------------------------------------------------
    @Test
    fun `register success triggers login and sets user logged in`() = runTest {
        val user = RegisterUser(
            email = "new@example.com",
            password = "secret",
            username = "NewUser"
        )

        // Registro sin fallo
        coJustRun { authRepository.registerUser(user) }

        // Login posterior sin fallo
        coJustRun { authRepository.loginUser(user.email, user.password) }

        val mockUser = mockkm.example.cookpilot.repository.AuthUser>()
        every { mockUser.id } returns "user123"
        every { authRepository.getCurrentUser() } returns mockUser
        every { authRepository.getUserData("user123") } returns emptyMap()

        viewModel.register(user)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.success)
        assertTrue(state.isLoggedIn)
        assertEquals("user123", state.userId)
    }

    // ---------------------------------------------------------------------
    // TEST 5: logout limpia el estado y llama al callback
    // ---------------------------------------------------------------------
    @Test
    fun `logout clears state and triggers callback`() = runTest {
        // Mocks para dependencias
        coJustRun { authRepository.logout() }
        coJustRun { preferencesManager.clearPreferences() }
        coJustRun { notificationScheduler.cancelAllNotifications() }

        var callbackCalled = false

        viewModel.logout {
            callbackCalled = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoggedIn)
        assertEquals(null, state.userId)
        assertEquals(null, state.userName)
        assertEquals(null, state.profilePictureId)
        assertTrue(callbackCalled)
    }

    // ---------------------------------------------------------------------
    // TEST 6: uploadProfilePicture sin usuario logeado
    // ---------------------------------------------------------------------
    @Test
    fun `uploadProfilePicture without user sets error`() = runTest {
        // Estado inicial sin userId
        assertEquals(null, viewModel.uiState.value.userId)

        val fakeUri = mockk<Uri>()

        viewModel.uploadProfilePicture(fakeUri)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("No user logged in", state.error)
    }
}
