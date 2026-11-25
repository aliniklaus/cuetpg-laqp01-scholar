package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.core.authStateFlow
import com.example.core.configureAppCheck
import com.example.core.signOut
import com.example.data.AiTutorRepository
import com.example.data.FirestoreSyncRepository
import com.example.data.StudyContentRepository
import com.example.model.AiStudyMode
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ExamPlayerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotebookVaultScreen
import com.example.ui.screens.NotesPyqScreen
import com.example.ui.screens.TestsScreen
import com.example.ui.theme.LaqpScholarTheme
import com.example.viewmodel.AiTutorViewModel
import com.example.viewmodel.ExamViewModel
import com.example.viewmodel.MainStudyViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

private enum class AppTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    NOTES("Notes & PYQ", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook, "nav_notes"),
    TESTS("Tests", Icons.Filled.Quiz, Icons.Outlined.Quiz, "nav_tests"),
    AI("AI Tutor", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_ai")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure App Check with launch intent BEFORE rendering UI or creating ViewModels
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            LaqpScholarTheme {
                AppNavigationRoot()
            }
        }
    }
}

@Composable
private fun AppNavigationRoot() {
    val auth = Firebase.auth
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        key(user.uid) {
            AuthenticatedAppShell(currentUser = user)
        }
    }
}

@Composable
private fun AuthenticatedAppShell(currentUser: FirebaseUser) {
    val uid = currentUser.uid
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val mainVm: MainStudyViewModel = viewModel(
        key = "main_$uid",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val db = FirebaseFirestore.getInstance(app.getString(R.string.firestore_database_id))
                MainStudyViewModel(
                    syncRepo = FirestoreSyncRepository(db),
                    contentRepo = StudyContentRepository(),
                    currentUserId = uid
                )
            }
        }
    )

    val examVm: ExamViewModel = viewModel(
        key = "exam_$uid",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val db = FirebaseFirestore.getInstance(app.getString(R.string.firestore_database_id))
                ExamViewModel(
                    contentRepo = StudyContentRepository(),
                    syncRepo = FirestoreSyncRepository(db),
                    currentUserId = uid
                )
            }
        }
    )

    val aiVm: AiTutorViewModel = viewModel(
        key = "ai_$uid",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val db = FirebaseFirestore.getInstance(app.getString(R.string.firestore_database_id))
                AiTutorViewModel(
                    aiRepo = AiTutorRepository(),
                    syncRepo = FirestoreSyncRepository(db),
                    currentUserId = uid
                )
            }
        }
    )

    val noteProgressState by mainVm.noteProgressState.collectAsStateWithLifecycle()
    val attemptsState by mainVm.testAttemptsState.collectAsStateWithLifecycle()
    val notebookLinksState by mainVm.notebookLinksState.collectAsStateWithLifecycle()
    val savedAiNotesState by mainVm.savedAiNotesState.collectAsStateWithLifecycle()
    val statusMessage by mainVm.statusMessage.collectAsStateWithLifecycle()
    val examSession by examVm.session.collectAsStateWithLifecycle()
    val aiUiState by aiVm.uiState.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var showNotebookVault by remember { mutableStateOf(false) }
    var selectedUnitFilter by remember { mutableStateOf("ALL") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        val msg = statusMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            mainVm.clearStatusMessage()
        }
    }

    LaunchedEffect(aiUiState.statusNotice) {
        val notice = aiUiState.statusNotice
        if (notice != null) {
            snackbarHostState.showSnackbar(notice)
            aiVm.clearStatusNotice()
        }
    }

    // Active Exam Player takes over fullscreen cleanly
    if (examSession.isActive) {
        ExamPlayerScreen(
            session = examSession,
            onSelectOption = examVm::selectOption,
            onClearResponse = examVm::clearResponse,
            onToggleMarkForReview = examVm::toggleMarkForReview,
            onGoToQuestion = examVm::goToQuestion,
            onPrevious = examVm::previousQuestion,
            onNext = examVm::nextQuestion,
            onSubmit = examVm::submitExam,
            onExit = examVm::exitSession,
            onAskAiForQuestion = { prompt ->
                examVm.exitSession()
                currentTab = AppTab.AI
                aiVm.askPreset(AiStudyMode.EXPLAIN, prompt)
            }
        )
        return
    }

    if (showNotebookVault) {
        NotebookVaultScreen(
            linksState = notebookLinksState,
            onBack = { showNotebookVault = false },
            onAddLink = mainVm::addNotebookLink,
            onUpdateLink = mainVm::updateNotebookLink,
            onTogglePin = mainVm::togglePinNotebookLink,
            onDeleteLink = mainVm::deleteNotebookLink
        )
        return
    }

    BackHandler(enabled = currentTab != AppTab.HOME) {
        currentTab = AppTab.HOME
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!useNavigationRail) {
                    NavigationBar {
                        AppTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail {
                        AppTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 860.dp)
                    ) {
                        val units = mainVm.contentRepo.getUnits()
                        val notes = mainVm.contentRepo.getNotes()
                        val pyqs = mainVm.contentRepo.getPyqQuestions()

                        when (currentTab) {
                            AppTab.HOME -> HomeScreen(
                                userDisplayName = currentUser.displayName ?: currentUser.email ?: "Scholar",
                                units = units,
                                allNotes = notes,
                                noteProgressState = noteProgressState,
                                attemptsState = attemptsState,
                                notebookLinksState = notebookLinksState,
                                onStartFullMock = examVm::startFullMock,
                                onOpenNotebookVault = { showNotebookVault = true },
                                onOpenUnitNotes = { unitId ->
                                    selectedUnitFilter = unitId
                                    currentTab = AppTab.NOTES
                                },
                                onDrillUnit = examVm::startUnitDrill,
                                onOpenAiTutor = { currentTab = AppTab.AI },
                                onSignOut = {
                                    signOut(
                                        credentialManager = credentialManager,
                                        scope = scope
                                    )
                                }
                            )

                            AppTab.NOTES -> NotesPyqScreen(
                                units = units,
                                notes = notes,
                                pyqs = pyqs,
                                noteProgressState = noteProgressState,
                                selectedUnitFilter = selectedUnitFilter,
                                onUnitFilterChange = { selectedUnitFilter = it },
                                onToggleBookmark = mainVm::toggleBookmark,
                                onToggleMastered = mainVm::toggleMastered,
                                onSavePersonalNote = mainVm::savePersonalNote,
                                onQuizUnit = { unit -> examVm.startUnitDrill(unit) },
                                onStartPyqQuiz = { yearTag -> examVm.startPyqPaper(yearTag) },
                                onAskAi = { prompt ->
                                    currentTab = AppTab.AI
                                    aiVm.askPreset(AiStudyMode.EXPLAIN, prompt)
                                }
                            )

                            AppTab.TESTS -> TestsScreen(
                                units = units,
                                attemptsState = attemptsState,
                                onStartFullMock = examVm::startFullMock,
                                onStartMixedDrill = examVm::startMixedDrill,
                                onStartUnitDrill = examVm::startUnitDrill,
                                onDeleteAttempt = mainVm::deleteTestAttempt
                            )

                            AppTab.AI -> AiAssistantScreen(
                                uiState = aiUiState,
                                savedNotesState = savedAiNotesState,
                                onSelectMode = aiVm::selectMode,
                                onInputChange = aiVm::updateInput,
                                onSendPrompt = aiVm::submitCurrentPrompt,
                                onAskPreset = aiVm::askPreset,
                                onSaveMessageCard = aiVm::saveMessageAsCard,
                                onDeleteSavedCard = mainVm::deleteSavedAiNote,
                                onLaunchGeneratedQuiz = { title, questions ->
                                    aiVm.clearGeneratedQuiz()
                                    examVm.startCustomAiQuiz(title, questions)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
