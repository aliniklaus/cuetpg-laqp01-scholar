package com.example.data

import com.example.core.OperationType
import com.example.core.handleFirestoreError
import com.example.model.NoteProgress
import com.example.model.NotebookLink
import com.example.model.SavedAiNote
import com.example.model.TestAttempt
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreSyncRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    private fun requireUserId(expectedUserId: String? = null): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Please sign in with Google before syncing study data.")
        if (expectedUserId != null) {
            check(uid == expectedUserId) { "Signed-in account changed; refusing cross-user write." }
        }
        return uid
    }

    fun observeNoteProgress(userId: String): Flow<List<NoteProgress>> =
        db.collection("users").document(userId).collection("noteProgress")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    NoteProgress::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    fun observeTestAttempts(userId: String): Flow<List<TestAttempt>> =
        db.collection("users").document(userId).collection("testAttempts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    TestAttempt::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    fun observeNotebookLinks(userId: String): Flow<List<NotebookLink>> =
        db.collection("users").document(userId).collection("notebookLinks")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    NotebookLink::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    fun observeSavedAiNotes(userId: String): Flow<List<SavedAiNote>> =
        db.collection("users").document(userId).collection("savedAiNotes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    SavedAiNote::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    suspend fun upsertNoteProgress(
        userId: String,
        noteId: String,
        unitId: String,
        bookmarked: Boolean,
        mastered: Boolean,
        personalNote: String?,
        alreadyExists: Boolean
    ): Result<Unit> {
        val path = "users/$userId/noteProgress/$noteId"
        return try {
            val uid = requireUserId(userId)
            require(noteId.matches(Regex("^[a-zA-Z0-9_\\-]{1,128}$"))) { "Invalid note ID" }
            require(unitId.length in 1..32) { "Invalid unit ID" }
            val trimmedNote = personalNote?.trim()?.take(2000)

            val docRef = db.collection("users").document(uid).collection("noteProgress").document(noteId)
            if (alreadyExists) {
                val updates = mutableMapOf<String, Any>(
                    "bookmarked" to bookmarked,
                    "mastered" to mastered,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (trimmedNote != null) {
                    updates["personalNote"] = trimmedNote
                }
                docRef.update(updates).await()
            } else {
                val payload = mutableMapOf<String, Any?>(
                    "userId" to uid,
                    "noteId" to noteId,
                    "unitId" to unitId,
                    "bookmarked" to bookmarked,
                    "mastered" to mastered,
                    "personalNote" to trimmedNote,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, if (alreadyExists) OperationType.UPDATE else OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun saveTestAttempt(
        userId: String,
        mode: String,
        title: String,
        unitFilter: String,
        score: Int,
        maxScore: Int,
        correctCount: Int,
        wrongCount: Int,
        unattemptedCount: Int,
        durationSeconds: Int,
        unitAccuracySummary: String?
    ): Result<Unit> {
        val attemptId = "att_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val path = "users/$userId/testAttempts/$attemptId"
        return try {
            val uid = requireUserId(userId)
            require(mode in setOf("MOCK", "PRACTICE", "AI_QUIZ")) { "Invalid attempt mode" }
            val cleanTitle = title.trim().ifEmpty { "LAQP01 Practice" }.take(140)
            val cleanUnit = unitFilter.trim().ifEmpty { "All Units" }.take(64)
            val cleanSummary = unitAccuracySummary?.trim()?.take(1000)

            val payload = mapOf<String, Any?>(
                "userId" to uid,
                "mode" to mode,
                "title" to cleanTitle,
                "unitFilter" to cleanUnit,
                "score" to score.coerceIn(-300, 300),
                "maxScore" to maxScore.coerceIn(1, 300),
                "correctCount" to correctCount.coerceIn(0, 75),
                "wrongCount" to wrongCount.coerceIn(0, 75),
                "unattemptedCount" to unattemptedCount.coerceIn(0, 75),
                "durationSeconds" to durationSeconds.coerceIn(0, 7200),
                "unitAccuracySummary" to cleanSummary,
                "createdAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            db.collection("users").document(uid).collection("testAttempts").document(attemptId)
                .set(payload)
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteTestAttempt(userId: String, attemptId: String): Result<Unit> {
        val path = "users/$userId/testAttempts/$attemptId"
        return try {
            val uid = requireUserId(userId)
            require(attemptId.isNotBlank()) { "Attempt ID cannot be blank" }
            db.collection("users").document(uid).collection("testAttempts").document(attemptId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun addNotebookLink(
        userId: String,
        title: String,
        url: String,
        unitTag: String,
        description: String?,
        pinned: Boolean = false
    ): Result<Unit> {
        val linkId = "nb_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val path = "users/$userId/notebookLinks/$linkId"
        return try {
            val uid = requireUserId(userId)
            val cleanTitle = title.trim().take(120)
            val cleanUrl = url.trim().take(500)
            val cleanUnit = unitTag.trim().ifEmpty { "All Units" }.take(40)
            val cleanDesc = description?.trim()?.takeIf { it.isNotEmpty() }?.take(400)

            require(cleanTitle.isNotEmpty()) { "Title is required." }
            require(cleanUrl.startsWith("https://") && cleanUrl.length >= 10) {
                "URL must start with https://"
            }

            val payload = mapOf<String, Any?>(
                "userId" to uid,
                "title" to cleanTitle,
                "url" to cleanUrl,
                "unitTag" to cleanUnit,
                "description" to cleanDesc,
                "pinned" to pinned,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            db.collection("users").document(uid).collection("notebookLinks").document(linkId)
                .set(payload)
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateNotebookLink(
        userId: String,
        linkId: String,
        title: String,
        url: String,
        unitTag: String,
        description: String?,
        pinned: Boolean
    ): Result<Unit> {
        val path = "users/$userId/notebookLinks/$linkId"
        return try {
            val uid = requireUserId(userId)
            val cleanTitle = title.trim().take(120)
            val cleanUrl = url.trim().take(500)
            val cleanUnit = unitTag.trim().ifEmpty { "All Units" }.take(40)
            val cleanDesc = description?.trim()?.take(400) ?: ""

            require(linkId.isNotBlank()) { "Link ID cannot be blank" }
            require(cleanTitle.isNotEmpty()) { "Title is required." }
            require(cleanUrl.startsWith("https://") && cleanUrl.length >= 10) {
                "URL must start with https://"
            }

            val updates = mapOf<String, Any>(
                "title" to cleanTitle,
                "url" to cleanUrl,
                "unitTag" to cleanUnit,
                "description" to cleanDesc,
                "pinned" to pinned,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid).collection("notebookLinks").document(linkId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun togglePinNotebookLink(
        userId: String,
        linkId: String,
        pinned: Boolean
    ): Result<Unit> {
        val path = "users/$userId/notebookLinks/$linkId"
        return try {
            val uid = requireUserId(userId)
            require(linkId.isNotBlank()) { "Link ID cannot be blank" }
            db.collection("users").document(uid).collection("notebookLinks").document(linkId)
                .update(
                    mapOf(
                        "pinned" to pinned,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteNotebookLink(userId: String, linkId: String): Result<Unit> {
        val path = "users/$userId/notebookLinks/$linkId"
        return try {
            val uid = requireUserId(userId)
            require(linkId.isNotBlank()) { "Link ID cannot be blank" }
            db.collection("users").document(uid).collection("notebookLinks").document(linkId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun saveAiNote(
        userId: String,
        mode: String,
        topic: String,
        unitTag: String,
        content: String
    ): Result<Unit> {
        val cardId = "ai_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val path = "users/$userId/savedAiNotes/$cardId"
        return try {
            val uid = requireUserId(userId)
            require(mode in setOf("EXPLAIN", "MNEMONIC", "TIMELINE", "COMPARISON")) {
                "Unsupported AI note mode"
            }
            val cleanTopic = topic.trim().ifEmpty { "LAQP01 Study Note" }.take(160)
            val cleanUnit = unitTag.trim().ifEmpty { "All Units" }.take(40)
            val cleanContent = content.trim().take(8000)
            require(cleanContent.isNotEmpty()) { "Content cannot be empty" }

            val payload = mapOf(
                "userId" to uid,
                "mode" to mode,
                "topic" to cleanTopic,
                "unitTag" to cleanUnit,
                "content" to cleanContent,
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid).collection("savedAiNotes").document(cardId)
                .set(payload)
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteAiNote(userId: String, cardId: String): Result<Unit> {
        val path = "users/$userId/savedAiNotes/$cardId"
        return try {
            val uid = requireUserId(userId)
            require(cardId.isNotBlank()) { "Card ID cannot be blank" }
            db.collection("users").document(uid).collection("savedAiNotes").document(cardId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }
}
