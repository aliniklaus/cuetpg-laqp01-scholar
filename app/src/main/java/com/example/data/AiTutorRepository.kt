package com.example.data

import com.example.model.AiStudyMode
import com.example.model.QuizQuestion
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.UUID

class AiTutorRepository {

    private val streamingModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are an expert academic tutor for NTA CUET PG English (paper code LAQP01). " +
                        "Provide accurate literary history, dates, authors, texts, Indian Writing in English, " +
                        "literary terms, comparative literature, literary theory/criticism, and current literary awards. " +
                        "Be structured, concise, exam-oriented, and highlight high-yield MCQ facts."
                )
            }
        )
    }

    private val quizSchema = Schema.obj(
        properties = mapOf(
            "quizTitle" to Schema.string(description = "Short descriptive title for the generated quiz"),
            "questions" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "prompt" to Schema.string(description = "CUET PG LAQP01 style multiple-choice question"),
                        "options" to Schema.array(
                            items = Schema.string(),
                            description = "Exactly 4 answer choices"
                        ),
                        "correctIndex" to Schema.integer(description = "0-based index (0..3) of the correct option"),
                        "explanation" to Schema.string(description = "Concise exam explanation citing author/text/concept"),
                        "unitId" to Schema.string(description = "Syllabus unit ID from unit_1 to unit_7")
                    )
                ),
                description = "List of generated multiple choice questions"
            )
        )
    )

    private val quizJsonModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = quizSchema
            },
            systemInstruction = content {
                text(
                    "You are an NTA CUET PG English (LAQP01) question setter. " +
                        "Create rigorous, unambiguous MCQs with 4 distinct options, accurate facts, " +
                        "and clear explanations suitable for +4/-1 exam preparation."
                )
            }
        )
    }

    fun streamStudyResponse(mode: AiStudyMode, userPrompt: String): Flow<String> = flow {
        val instructionPrefix = when (mode) {
            AiStudyMode.EXPLAIN ->
                "Explain clearly for CUET PG LAQP01 exam prep. Include key texts, theorists/authors, and common MCQ traps:\n\n"
            AiStudyMode.MNEMONIC ->
                "Create memorable mnemonics, acronyms, or vivid association hooks to retain this for CUET PG LAQP01:\n\n"
            AiStudyMode.TIMELINE ->
                "Build a clean chronological timeline with exact years, movements, landmark works, and authors for:\n\n"
            AiStudyMode.COMPARISON ->
                "Provide a structured side-by-side comparison (key works, core themes/arguments, differences, and exam takeaways) for:\n\n"
            AiStudyMode.QUIZ_GEN ->
                "Summarize key exam points for:\n\n"
        }

        val fullPrompt = instructionPrefix + userPrompt.trim()
        val builder = StringBuilder()
        withTimeout(60_000L) {
            streamingModel.generateContentStream(fullPrompt).collect { chunk ->
                val text = chunk.text.orEmpty()
                if (text.isNotEmpty()) {
                    builder.append(text)
                    emit(builder.toString())
                }
            }
        }
        if (builder.isBlank()) {
            throw IllegalStateException("AI Tutor returned an empty response.")
        }
    }

    suspend fun generateCustomQuiz(
        topicOrNotes: String,
        questionCount: Int = 5
    ): Pair<String, List<QuizQuestion>> {
        val clampedCount = questionCount.coerceIn(3, 10)
        val prompt = """
            Generate $clampedCount CUET PG English (LAQP01) practice MCQs based on this topic or study excerpt:
            "$topicOrNotes"
            
            Ensure each question has exactly 4 options, correctIndex between 0 and 3, a concise explanation, and unitId between unit_1 and unit_7.
        """.trimIndent()

        val response = withTimeout(60_000L) {
            quizJsonModel.generateContent(prompt)
        }
        val rawJson = response.text
            ?: throw IllegalStateException("AI Tutor returned empty quiz JSON.")

        val root = JSONObject(rawJson)
        val quizTitle = root.optString("quizTitle", "AI Custom LAQP01 Quiz").ifBlank { "AI Custom LAQP01 Quiz" }
        val jsonQuestions = root.getJSONArray("questions")
        val parsed = mutableListOf<QuizQuestion>()

        for (i in 0 until jsonQuestions.length()) {
            val qObj = jsonQuestions.getJSONObject(i)
            val optionsArray = qObj.getJSONArray("options")
            val options = (0 until optionsArray.length()).map { optionsArray.getString(it) }
            if (options.size < 2) continue
            val correctIdx = qObj.optInt("correctIndex", 0).coerceIn(0, options.lastIndex)
            val unitId = qObj.optString("unitId", "unit_1").takeIf {
                it in setOf("unit_1", "unit_2", "unit_3", "unit_4", "unit_5", "unit_6", "unit_7")
            } ?: "unit_1"
            parsed.add(
                QuizQuestion(
                    id = "ai_${UUID.randomUUID().toString().take(8)}_$i",
                    unitId = unitId,
                    yearTag = "AI Generated",
                    prompt = qObj.getString("prompt"),
                    options = options.take(4),
                    correctIndex = correctIdx,
                    explanation = qObj.optString("explanation", "Review the syllabus module for details."),
                    difficulty = "AI Custom"
                )
            )
        }

        if (parsed.isEmpty()) {
            throw IllegalStateException("No valid quiz questions were generated.")
        }
        return quizTitle to parsed
    }
}
