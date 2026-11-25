package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirestoreSyncRepository
import com.example.data.StudyContentRepository
import com.example.model.NoteProgress
import com.example.model.NotebookLink
import com.example.model.SavedAiNote
import com.example.model.StudyNote
import com.example.model.TestAttempt
import com.example.model.UiState
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainStudyViewModel(
    private val syncRepo: FirestoreSyncRepository,
    val contentRepo: StudyContentRepository,
    private val currentUserId: String
) : ViewModel() {

    val noteProgressState: StateFlow<UiState<List<NoteProgress>>> =
        syncRepo.observeNoteProgress(currentUserId)
            .map<List<NoteProgress>, UiState<List<NoteProgress>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = unwrapFirestoreException(error)
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Failed observing noteProgress", root)
                emit(UiState.Error(root.message ?: "Unable to load note progress"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val testAttemptsState: StateFlow<UiState<List<TestAttempt>>> =
        syncRepo.observeTestAttempts(currentUserId)
            .map<List<TestAttempt>, UiState<List<TestAttempt>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = unwrapFirestoreException(error)
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Failed observing testAttempts", root)
                emit(UiState.Error(root.message ?: "Unable to load test history"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val notebookLinksState: StateFlow<UiState<List<NotebookLink>>> =
        syncRepo.observeNotebookLinks(currentUserId)
            .map<List<NotebookLink>, UiState<List<NotebookLink>>> { list ->
                val sorted = list.sortedByDescending { it.pinned }
                UiState.Success(sorted)
            }
            .catch { error ->
                val firestoreError = unwrapFirestoreException(error)
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Failed observing notebookLinks", root)
                emit(UiState.Error(root.message ?: "Unable to load NotebookLM vault"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    val savedAiNotesState: StateFlow<UiState<List<SavedAiNote>>> =
        syncRepo.observeSavedAiNotes(currentUserId)
            .map<List<SavedAiNote>, UiState<List<SavedAiNote>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = unwrapFirestoreException(error)
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Failed observing savedAiNotes", root)
                emit(UiState.Error(root.message ?: "Unable to load saved AI study cards"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun toggleBookmark(note: StudyNote, existing: NoteProgress?) {
        viewModelScope.launch {
            val nextBookmarked = !(existing?.bookmarked ?: false)
            val result = syncRepo.upsertNoteProgress(
                userId = currentUserId,
                noteId = note.id,
                unitId = note.unitId,
                bookmarked = nextBookmarked,
                mastered = existing?.mastered ?: false,
                personalNote = existing?.personalNote,
                alreadyExists = existing != null
            )
            result.onFailure {
                _statusMessage.value = it.localizedMessage ?: "Failed to update bookmark"
            }
        }
    }

    fun toggleMastered(note: StudyNote, existing: NoteProgress?) {
        viewModelScope.launch {
            val nextMastered = !(existing?.mastered ?: false)
            val result = syncRepo.upsertNoteProgress(
                userId = currentUserId,
                noteId = note.id,
                unitId = note.unitId,
                bookmarked = existing?.bookmarked ?: false,
                mastered = nextMastered,
                personalNote = existing?.personalNote,
                alreadyExists = existing != null
            )
            result.onSuccess {
                _statusMessage.value = if (nextMastered) "Marked as mastered" else "Unmarked mastered"
            }.onFailure {
                _statusMessage.value = it.localizedMessage ?: "Failed to update mastery"
            }
        }
    }

    fun savePersonalNote(note: StudyNote, text: String, existing: NoteProgress?) {
        viewModelScope.launch {
            val result = syncRepo.upsertNoteProgress(
                userId = currentUserId,
                noteId = note.id,
                unitId = note.unitId,
                bookmarked = existing?.bookmarked ?: false,
                mastered = existing?.mastered ?: false,
                personalNote = text,
                alreadyExists = existing != null
            )
            result.onSuccess {
                _statusMessage.value = "Personal annotation saved"
            }.onFailure {
                _statusMessage.value = it.localizedMessage ?: "Could not save annotation"
            }
        }
    }

    fun addNotebookLink(
        title: String,
        url: String,
        unitTag: String,
        description: String?,
        pinned: Boolean
    ) {
        viewModelScope.launch {
            val result = syncRepo.addNotebookLink(
                userId = currentUserId,
                title = title,
                url = url,
                unitTag = unitTag,
                description = description,
                pinned = pinned
            )
            result.onSuccess {
                _statusMessage.value = "NotebookLM link saved to vault"
            }.onFailure {
                _statusMessage.value = it.localizedMessage ?: "Invalid NotebookLM link"
            }
        }
    }

    fun updateNotebookLink(
        linkId: String,
        title: String,
        url: String,
        unitTag: String,
        description: String?,
        pinned: Boolean
    ) {
        viewModelScope.launch {
            val result = syncRepo.updateNotebookLink(
                userId = currentUserId,
                linkId = linkId,
                title = title,
                url = url,
                unitTag = unitTag,
                description = description,
                pinned = pinned
            )
            result.onSuccess {
                _statusMessage.value = "NotebookLM link updated"
            }.onFailure {
                _statusMessage.value = it.localizedMessage ?: "Could not update link"
            }
        }
    }

    fun togglePinNotebookLink(link: NotebookLink) {
        viewModelScope.launch {
            val result = syncRepo.togglePinNotebookLink(
                userId = currentUserId,
                linkId = link.id,
                pinned = !link.pinned
            )
            result.onFailure {
                _statusMessage.value = it.localizedMessage ?: "Could not update pin"
            }
        }
    }

    fun deleteNotebookLink(linkId: String) {
        viewModelScope.launch {
            syncRepo.deleteNotebookLink(currentUserId, linkId)
                .onSuccess { _statusMessage.value = "NotebookLM link removed" }
                .onFailure { _statusMessage.value = it.localizedMessage ?: "Could not delete link" }
        }
    }

    fun deleteTestAttempt(attemptId: String) {
        viewModelScope.launch {
            syncRepo.deleteTestAttempt(currentUserId, attemptId)
                .onSuccess { _statusMessage.value = "Test record deleted" }
                .onFailure { _statusMessage.value = it.localizedMessage ?: "Could not delete record" }
        }
    }

    fun deleteSavedAiNote(cardId: String) {
        viewModelScope.launch {
            syncRepo.deleteAiNote(currentUserId, cardId)
                .onSuccess { _statusMessage.value = "Study card deleted" }
                .onFailure { _statusMessage.value = it.localizedMessage ?: "Could not delete study card" }
        }
    }

    private fun unwrapFirestoreException(error: Throwable): FirebaseFirestoreException? =
        generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()

    private companion object {
        const val TAG = "MainStudyVM"
    }
}
