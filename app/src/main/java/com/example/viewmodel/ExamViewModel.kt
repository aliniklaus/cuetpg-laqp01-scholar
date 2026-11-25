package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirestoreSyncRepository
import com.example.data.StudyContentRepository
import com.example.model.ExamMode
import com.example.model.QuestionMarkState
import com.example.model.QuizQuestion
import com.example.model.SyllabusUnit
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExamSessionState(
    val isActive: Boolean = false,
    val isSubmitted: Boolean = false,
    val mode: ExamMode = ExamMode.PRACTICE,
    val title: String = "",
    val unitFilter: String = "All Units",
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val markedForReview: Set<Int> = emptySet(),
    val visitedIndices: Set<Int> = emptySet(),
    val remainingSeconds: Int = 0,
    val elapsedSeconds: Int = 0,
    val isTimedCountdown: Boolean = false,
    val isSavingResult: Boolean = false,
    val resultSaved: Boolean = false,
    val saveError: String? = null
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentIndex)

    val correctCount: Int
        get() = questions.indices.count { idx ->
            selectedAnswers[idx] == questions[idx].correctIndex
        }

    val wrongCount: Int
        get() = questions.indices.count { idx ->
            val ans = selectedAnswers[idx]
            ans != null && ans != questions[idx].correctIndex
        }

    val unattemptedCount: Int
        get() = questions.size - selectedAnswers.size

    val score: Int
        get() = (correctCount * 4) - wrongCount

    val maxScore: Int
        get() = questions.size * 4

    fun markStateFor(index: Int): QuestionMarkState {
        val answered = selectedAnswers.containsKey(index)
        val marked = markedForReview.contains(index)
        return when {
            answered && marked -> QuestionMarkState.ANSWERED_AND_MARKED
            marked -> QuestionMarkState.MARKED_REVIEW
            answered -> QuestionMarkState.ANSWERED
            else -> QuestionMarkState.UNVISITED
        }
    }
}

class ExamViewModel(
    private val contentRepo: StudyContentRepository,
    private val syncRepo: FirestoreSyncRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _session = MutableStateFlow(ExamSessionState())
    val session: StateFlow<ExamSessionState> = _session.asStateFlow()

    private var timerJob: Job? = null

    fun startFullMock() {
        val questions = contentRepo.getFullMock75Questions()
        launchSession(
            mode = ExamMode.MOCK,
            title = "CUET PG LAQP01 Full Mock (75 Qs)",
            unitFilter = "All Units",
            questions = questions,
            timedCountdown = true,
            initialSeconds = 90 * 60
        )
    }

    fun startUnitDrill(unit: SyllabusUnit) {
        val questions = contentRepo.getUnitPracticeQuestions(unit.id, count = 10)
        launchSession(
            mode = ExamMode.PRACTICE,
            title = "${unit.shortTitle} Drill",
            unitFilter = "Unit ${unit.number}",
            questions = questions,
            timedCountdown = false,
            initialSeconds = 0
        )
    }

    fun startMixedDrill() {
        val questions = contentRepo.getMixedQuickDrill(count = 10)
        launchSession(
            mode = ExamMode.PRACTICE,
            title = "Quick 10-Q Mixed LAQP01 Drill",
            unitFilter = "All Units",
            questions = questions,
            timedCountdown = false,
            initialSeconds = 0
        )
    }

    fun startPyqPaper(yearTag: String? = null) {
        val allPyqs = contentRepo.getPyqQuestions()
        val filtered = if (yearTag == null) allPyqs else allPyqs.filter { it.yearTag == yearTag }
        val title = if (yearTag == null) "CUET PG LAQP01 PYQ Set (All)" else "CUET PG LAQP01 $yearTag Paper"
        launchSession(
            mode = ExamMode.PRACTICE,
            title = title,
            unitFilter = yearTag ?: "PYQs",
            questions = filtered,
            timedCountdown = false,
            initialSeconds = 0
        )
    }

    fun startCustomAiQuiz(title: String, questions: List<QuizQuestion>) {
        if (questions.isEmpty()) return
        launchSession(
            mode = ExamMode.AI_QUIZ,
            title = title,
            unitFilter = "AI Custom",
            questions = questions,
            timedCountdown = false,
            initialSeconds = 0
        )
    }

    private fun launchSession(
        mode: ExamMode,
        title: String,
        unitFilter: String,
        questions: List<QuizQuestion>,
        timedCountdown: Boolean,
        initialSeconds: Int
    ) {
        timerJob?.cancel()
        _session.value = ExamSessionState(
            isActive = true,
            isSubmitted = false,
            mode = mode,
            title = title,
            unitFilter = unitFilter,
            questions = questions,
            currentIndex = 0,
            selectedAnswers = emptyMap(),
            markedForReview = emptySet(),
            visitedIndices = if (questions.isNotEmpty()) setOf(0) else emptySet(),
            remainingSeconds = initialSeconds,
            elapsedSeconds = 0,
            isTimedCountdown = timedCountdown
        )

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _session.value
                if (!current.isActive || current.isSubmitted) break
                if (current.isTimedCountdown) {
                    val nextRemaining = (current.remainingSeconds - 1).coerceAtLeast(0)
                    _session.update {
                        it.copy(
                            remainingSeconds = nextRemaining,
                            elapsedSeconds = it.elapsedSeconds + 1
                        )
                    }
                    if (nextRemaining == 0) {
                        submitExam()
                        break
                    }
                } else {
                    _session.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun selectOption(optionIndex: Int) {
        val current = _session.value
        if (!current.isActive || current.isSubmitted) return
        val idx = current.currentIndex
        _session.update {
            it.copy(
                selectedAnswers = it.selectedAnswers + (idx to optionIndex),
                visitedIndices = it.visitedIndices + idx
            )
        }
    }

    fun clearResponse() {
        val current = _session.value
        if (!current.isActive || current.isSubmitted) return
        val idx = current.currentIndex
        _session.update {
            it.copy(selectedAnswers = it.selectedAnswers - idx)
        }
    }

    fun toggleMarkForReview() {
        val current = _session.value
        if (!current.isActive || current.isSubmitted) return
        val idx = current.currentIndex
        _session.update {
            val nextSet = if (idx in it.markedForReview) it.markedForReview - idx else it.markedForReview + idx
            it.copy(markedForReview = nextSet)
        }
    }

    fun goToQuestion(index: Int) {
        val current = _session.value
        if (index !in current.questions.indices) return
        _session.update {
            it.copy(
                currentIndex = index,
                visitedIndices = it.visitedIndices + index
            )
        }
    }

    fun nextQuestion() {
        val current = _session.value
        if (current.currentIndex < current.questions.lastIndex) {
            goToQuestion(current.currentIndex + 1)
        }
    }

    fun previousQuestion() {
        val current = _session.value
        if (current.currentIndex > 0) {
            goToQuestion(current.currentIndex - 1)
        }
    }

    fun submitExam() {
        val snapshot = _session.value
        if (!snapshot.isActive || snapshot.isSubmitted) return
        timerJob?.cancel()

        val unitGroups = snapshot.questions.indices.groupBy { snapshot.questions[it].unitId }
        val unitSummary = unitGroups.entries
            .sortedBy { it.key }
            .joinToString(" • ") { (unitId, indices) ->
                val unitNum = unitId.removePrefix("unit_")
                val correctInUnit = indices.count { idx ->
                    snapshot.selectedAnswers[idx] == snapshot.questions[idx].correctIndex
                }
                val pct = if (indices.isNotEmpty()) (correctInUnit * 100) / indices.size else 0
                "U$unitNum: $pct%"
            }

        _session.update {
            it.copy(
                isSubmitted = true,
                isSavingResult = true,
                saveError = null
            )
        }

        viewModelScope.launch {
            val result = syncRepo.saveTestAttempt(
                userId = currentUserId,
                mode = snapshot.mode.wireValue,
                title = snapshot.title,
                unitFilter = snapshot.unitFilter,
                score = snapshot.score,
                maxScore = snapshot.maxScore.coerceAtLeast(1),
                correctCount = snapshot.correctCount,
                wrongCount = snapshot.wrongCount,
                unattemptedCount = snapshot.unattemptedCount,
                durationSeconds = snapshot.elapsedSeconds,
                unitAccuracySummary = unitSummary.ifBlank { null }
            )
            _session.update { state ->
                state.copy(
                    isSavingResult = false,
                    resultSaved = result.isSuccess,
                    saveError = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    fun exitSession() {
        timerJob?.cancel()
        _session.value = ExamSessionState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
