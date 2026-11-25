package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiTutorRepository
import com.example.data.FirestoreSyncRepository
import com.example.model.AiStudyMode
import com.example.model.QuizQuestion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AiMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val mode: AiStudyMode,
    val text: String,
    val unitTag: String = "All Units",
    val isSaved: Boolean = false
)

data class GeneratedQuizReady(
    val title: String,
    val questions: List<QuizQuestion>
)

data class AiTutorUiState(
    val selectedMode: AiStudyMode = AiStudyMode.EXPLAIN,
    val inputText: String = "",
    val messages: List<AiMessage> = emptyList(),
    val isLoading: Boolean = false,
    val streamingText: String = "",
    val activeModeForStream: AiStudyMode = AiStudyMode.EXPLAIN,
    val activeTopicForStream: String = "",
    val errorMessage: String? = null,
    val generatedQuiz: GeneratedQuizReady? = null,
    val statusNotice: String? = null
)

class AiTutorViewModel(
    private val aiRepo: AiTutorRepository,
    private val syncRepo: FirestoreSyncRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    fun selectMode(mode: AiStudyMode) {
        _uiState.update { it.copy(selectedMode = mode, errorMessage = null) }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun clearStatusNotice() {
        _uiState.update { it.copy(statusNotice = null) }
    }

    fun clearGeneratedQuiz() {
        _uiState.update { it.copy(generatedQuiz = null) }
    }

    fun askPreset(mode: AiStudyMode, prompt: String) {
        _uiState.update { it.copy(selectedMode = mode, inputText = prompt, errorMessage = null) }
        submitCurrentPrompt()
    }

    fun submitCurrentPrompt() {
        val state = _uiState.value
        val prompt = state.inputText.trim()
        if (prompt.isEmpty() || state.isLoading) return

        if (state.selectedMode == AiStudyMode.QUIZ_GEN) {
            generateCustomQuiz(prompt)
            return
        }

        val userMsg = AiMessage(isUser = true, mode = state.selectedMode, text = prompt)
        _uiState.update {
            it.copy(
                inputText = "",
                messages = it.messages + userMsg,
                isLoading = true,
                streamingText = "",
                activeModeForStream = state.selectedMode,
                activeTopicForStream = prompt,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            var latestText = ""
            try {
                aiRepo.streamStudyResponse(state.selectedMode, prompt).collect { partial ->
                    latestText = partial
                    _uiState.update { it.copy(streamingText = partial) }
                }
                val finalMsg = AiMessage(
                    isUser = false,
                    mode = state.selectedMode,
                    text = latestText,
                    unitTag = inferUnitTag(prompt)
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        streamingText = "",
                        messages = it.messages + finalMsg
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Tutor timed out", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        streamingText = "",
                        errorMessage = "Request timed out after 60 seconds. Please retry with a shorter prompt."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Tutor streaming failed", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        streamingText = "",
                        errorMessage = e.localizedMessage ?: "AI Assistant failed to respond."
                    )
                }
            }
        }
    }

    fun generateCustomQuiz(topicOrNotes: String, count: Int = 5) {
        val cleanTopic = topicOrNotes.trim()
        if (cleanTopic.isEmpty() || _uiState.value.isLoading) return

        val userMsg = AiMessage(isUser = true, mode = AiStudyMode.QUIZ_GEN, text = "Generate $count MCQs: $cleanTopic")
        _uiState.update {
            it.copy(
                inputText = "",
                messages = it.messages + userMsg,
                isLoading = true,
                streamingText = "",
                errorMessage = null,
                generatedQuiz = null
            )
        }

        viewModelScope.launch {
            try {
                val (quizTitle, questions) = aiRepo.generateCustomQuiz(cleanTopic, count)
                val summaryMsg = AiMessage(
                    isUser = false,
                    mode = AiStudyMode.QUIZ_GEN,
                    text = "Generated ${questions.size} LAQP01 practice MCQs for \"$quizTitle\". Tap 'Launch Generated Quiz' below to practice with +4/-1 scoring."
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        messages = it.messages + summaryMsg,
                        generatedQuiz = GeneratedQuizReady(quizTitle, questions)
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Quiz generation timed out", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Quiz generation timed out after 60 seconds. Please try again."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Quiz generation failed", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to generate practice quiz."
                    )
                }
            }
        }
    }

    fun saveMessageAsCard(message: AiMessage, topicHint: String) {
        val wireMode = when (message.mode) {
            AiStudyMode.EXPLAIN, AiStudyMode.QUIZ_GEN -> "EXPLAIN"
            AiStudyMode.MNEMONIC -> "MNEMONIC"
            AiStudyMode.TIMELINE -> "TIMELINE"
            AiStudyMode.COMPARISON -> "COMPARISON"
        }
        viewModelScope.launch {
            val result = syncRepo.saveAiNote(
                userId = currentUserId,
                mode = wireMode,
                topic = topicHint.ifBlank { "LAQP01 Study Note" },
                unitTag = message.unitTag,
                content = message.text
            )
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.map {
                            if (it.id == message.id) it.copy(isSaved = true) else it
                        },
                        statusNotice = "Saved to AI Study Cards"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(statusNotice = err.localizedMessage ?: "Could not save study card")
                }
            }
        }
    }

    private fun inferUnitTag(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            "unit 2" in lower || "indian writing" in lower || "kanthapura" in lower || "ezekiel" in lower -> "Unit 2"
            "unit 3" in lower || "dissociation" in lower || "fallacy" in lower || "sonnet" in lower -> "Unit 3"
            "unit 4" in lower || "picaresque" in lower || "bildungsroman" in lower || "absurd" in lower -> "Unit 4"
            "unit 5" in lower || "translation" in lower || "venuti" in lower || "nida" in lower -> "Unit 5"
            "unit 6" in lower || "derrida" in lower || "spivak" in lower || "aristotle" in lower || "feminis" in lower -> "Unit 6"
            "unit 7" in lower || "booker" in lower || "nobel" in lower || "jnanpith" in lower -> "Unit 7"
            else -> "Unit 1"
        }
    }

    private companion object {
        const val TAG = "AiTutorVM"
    }
}
