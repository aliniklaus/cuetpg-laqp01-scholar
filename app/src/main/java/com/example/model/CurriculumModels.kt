package com.example.model

enum class ExamMode(val wireValue: String, val label: String) {
    MOCK("MOCK", "Full Mock (75 Qs)"),
    PRACTICE("PRACTICE", "Practice Drill"),
    AI_QUIZ("AI_QUIZ", "AI Custom Quiz")
}

enum class AiStudyMode(val wireValue: String, val label: String, val shortHint: String) {
    EXPLAIN("EXPLAIN", "Explain", "Clarify PYQ answers & literary theory"),
    QUIZ_GEN("QUIZ_GEN", "Quiz Gen", "Generate playable LAQP01 MCQs"),
    MNEMONIC("MNEMONIC", "Mnemonics", "Build memory hooks & acronyms"),
    TIMELINE("TIMELINE", "Timeline", "Chronological movements & dates"),
    COMPARISON("COMPARISON", "Compare", "Side-by-side author & text analysis")
}

data class SyllabusUnit(
    val id: String,
    val number: Int,
    val title: String,
    val shortTitle: String,
    val subtitle: String,
    val keyTopics: List<String>
)

data class StudyNote(
    val id: String,
    val unitId: String,
    val title: String,
    val eraOrTag: String,
    val summary: String,
    val keyAuthors: List<String>,
    val keyTexts: List<String>,
    val highYieldPoints: List<String>,
    val readMinutes: Int = 5
)

data class QuizQuestion(
    val id: String,
    val unitId: String,
    val yearTag: String? = null,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val difficulty: String = "Standard"
)

enum class QuestionMarkState {
    UNVISITED,
    ANSWERED,
    MARKED_REVIEW,
    ANSWERED_AND_MARKED
}

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
