package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class NoteProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val noteId: String = "",
    val unitId: String = "",
    val bookmarked: Boolean = false,
    val mastered: Boolean = false,
    val personalNote: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class TestAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val mode: String = ExamMode.PRACTICE.wireValue,
    val title: String = "",
    val unitFilter: String = "",
    val score: Int = 0,
    val maxScore: Int = 300,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val unattemptedCount: Int = 0,
    val durationSeconds: Int = 0,
    val unitAccuracySummary: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class NotebookLink(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val url: String = "",
    val unitTag: String = "All Units",
    val description: String? = null,
    val pinned: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SavedAiNote(
    @DocumentId val id: String = "",
    val userId: String = "",
    val mode: String = AiStudyMode.EXPLAIN.wireValue,
    val topic: String = "",
    val unitTag: String = "All Units",
    val content: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
