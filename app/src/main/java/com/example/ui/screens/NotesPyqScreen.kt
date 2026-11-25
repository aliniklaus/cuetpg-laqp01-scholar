package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.NoteProgress
import com.example.model.QuizQuestion
import com.example.model.StudyNote
import com.example.model.SyllabusUnit
import com.example.model.UiState
import com.example.ui.theme.MonoBadgeStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPyqScreen(
    units: List<SyllabusUnit>,
    notes: List<StudyNote>,
    pyqs: List<QuizQuestion>,
    noteProgressState: UiState<List<NoteProgress>>,
    selectedUnitFilter: String,
    onUnitFilterChange: (String) -> Unit,
    onToggleBookmark: (StudyNote, NoteProgress?) -> Unit,
    onToggleMastered: (StudyNote, NoteProgress?) -> Unit,
    onSavePersonalNote: (StudyNote, String, NoteProgress?) -> Unit,
    onQuizUnit: (SyllabusUnit) -> Unit,
    onStartPyqQuiz: (String?) -> Unit,
    onAskAi: (String) -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Notes, 1 = PYQs
    var searchQuery by remember { mutableStateOf("") }
    var bookmarkedOnly by remember { mutableStateOf(false) }
    var yearFilter by remember { mutableStateOf("All") }
    var openNote by remember { mutableStateOf<StudyNote?>(null) }

    val progressByNoteId = remember(noteProgressState) {
        ((noteProgressState as? UiState.Success)?.data.orEmpty()).associateBy { it.noteId }
    }

    val pyqSelections = remember { mutableStateMapOf<String, Int>() }

    val activeReaderNote = openNote
    if (activeReaderNote != null) {
        NoteReaderView(
            note = activeReaderNote,
            unit = units.firstOrNull { it.id == activeReaderNote.unitId },
            progress = progressByNoteId[activeReaderNote.id],
            onBack = { openNote = null },
            onToggleBookmark = { onToggleBookmark(activeReaderNote, progressByNoteId[activeReaderNote.id]) },
            onToggleMastered = { onToggleMastered(activeReaderNote, progressByNoteId[activeReaderNote.id]) },
            onSavePersonalNote = { text ->
                onSavePersonalNote(activeReaderNote, text, progressByNoteId[activeReaderNote.id])
            },
            onQuizUnit = {
                val unit = units.firstOrNull { it.id == activeReaderNote.unitId }
                if (unit != null) {
                    openNote = null
                    onQuizUnit(unit)
                }
            },
            onAskAi = {
                val prompt = "Explain key LAQP01 exam takeaways, chronology, and likely MCQ traps for: ${activeReaderNote.title} (${activeReaderNote.keyAuthors.joinToString(", ")})"
                openNote = null
                onAskAi(prompt)
            }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header + Segmented Switcher
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Notes & Previous Year Papers",
                    style = MaterialTheme.typography.headlineSmall
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        modifier = Modifier.testTag("tab_study_notes")
                    ) {
                        Text("7-Unit Notes (${notes.size})")
                    }
                    SegmentedButton(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        modifier = Modifier.testTag("tab_pyq_bank")
                    ) {
                        Text("PYQ Bank (${pyqs.size})")
                    }
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            if (activeTab == 0) "Search authors, movements, literary terms..."
                            else "Search previous year questions..."
                        )
                    },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_search_input")
                )
            }
        }

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedUnitFilter == "ALL" && !bookmarkedOnly,
                    onClick = {
                        onUnitFilterChange("ALL")
                        bookmarkedOnly = false
                    },
                    label = { Text("All Units") }
                )
            }
            if (activeTab == 0) {
                item {
                    FilterChip(
                        selected = bookmarkedOnly,
                        onClick = { bookmarkedOnly = !bookmarkedOnly },
                        label = { Text("Bookmarked") }
                    )
                }
            } else {
                items(listOf("All", "PYQ 2024", "PYQ 2023", "PYQ 2022")) { year ->
                    FilterChip(
                        selected = yearFilter == year,
                        onClick = { yearFilter = year },
                        label = { Text(year) }
                    )
                }
            }
            items(units, key = { it.id }) { unit ->
                FilterChip(
                    selected = selectedUnitFilter == unit.id,
                    onClick = {
                        onUnitFilterChange(if (selectedUnitFilter == unit.id) "ALL" else unit.id)
                    },
                    label = { Text("Unit ${unit.number}") }
                )
            }
        }

        if (activeTab == 0) {
            val filteredNotes = remember(notes, selectedUnitFilter, bookmarkedOnly, searchQuery, progressByNoteId) {
                notes.filter { note ->
                    val matchesUnit = selectedUnitFilter == "ALL" || note.unitId == selectedUnitFilter
                    val prog = progressByNoteId[note.id]
                    val matchesBookmark = !bookmarkedOnly || (prog?.bookmarked == true)
                    val q = searchQuery.trim().lowercase()
                    val matchesSearch = q.isEmpty() ||
                        note.title.lowercase().contains(q) ||
                        note.summary.lowercase().contains(q) ||
                        note.keyAuthors.any { it.lowercase().contains(q) } ||
                        note.keyTexts.any { it.lowercase().contains(q) } ||
                        note.highYieldPoints.any { it.lowercase().contains(q) }
                    matchesUnit && matchesBookmark && matchesSearch
                }
            }

            if (filteredNotes.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No study modules match your current filter.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        val prog = progressByNoteId[note.id]
                        val unitNum = units.firstOrNull { it.id == note.unitId }?.number ?: 1
                        StudyNoteCard(
                            note = note,
                            unitNumber = unitNum,
                            bookmarked = prog?.bookmarked == true,
                            mastered = prog?.mastered == true,
                            onClick = { openNote = note },
                            onToggleBookmark = { onToggleBookmark(note, prog) },
                            onToggleMastered = { onToggleMastered(note, prog) }
                        )
                    }
                }
            }
        } else {
            val filteredPyqs = remember(pyqs, selectedUnitFilter, yearFilter, searchQuery) {
                pyqs.filter { qItem ->
                    val matchesUnit = selectedUnitFilter == "ALL" || qItem.unitId == selectedUnitFilter
                    val matchesYear = yearFilter == "All" || qItem.yearTag == yearFilter
                    val q = searchQuery.trim().lowercase()
                    val matchesSearch = q.isEmpty() ||
                        qItem.prompt.lowercase().contains(q) ||
                        qItem.explanation.lowercase().contains(q)
                    matchesUnit && matchesYear && matchesSearch
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Practice PYQs in Exam Mode",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Attempt ${if (yearFilter == "All") "all ${pyqs.size}" else yearFilter} questions with +4 / -1 scoring",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = {
                                    onStartPyqQuiz(if (yearFilter == "All") null else yearFilter)
                                },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("start_pyq_quiz_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Quiz")
                            }
                        }
                    }
                }

                items(filteredPyqs, key = { it.id }) { q ->
                    PyqInteractiveCard(
                        question = q,
                        selectedOption = pyqSelections[q.id],
                        onSelectOption = { optIdx -> pyqSelections[q.id] = optIdx },
                        onAskAi = {
                            val prompt = "Explain this CUET PG LAQP01 PYQ (${q.yearTag ?: "PYQ"}) in detail and highlight why the other options are incorrect:\n\nQuestion: ${q.prompt}\nCorrect Answer: ${q.options[q.correctIndex]}"
                            onAskAi(prompt)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudyNoteCard(
    note: StudyNote,
    unitNumber: Int,
    bookmarked: Boolean,
    mastered: Boolean,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleMastered: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.dp,
            color = if (mastered) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("study_note_card_${note.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "UNIT $unitNumber",
                            style = MonoBadgeStyle,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "${note.eraOrTag} • ${note.readMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = onToggleBookmark) {
                        Icon(
                            imageVector = if (bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = if (bookmarked) "Remove bookmark" else "Bookmark note",
                            tint = if (bookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onToggleMastered) {
                        Icon(
                            imageVector = if (mastered) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = if (mastered) "Mastered" else "Mark mastered",
                            tint = if (mastered) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = note.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${note.highYieldPoints.size} high-yield takeaways • Tap to open full module",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun NoteReaderView(
    note: StudyNote,
    unit: SyllabusUnit?,
    progress: NoteProgress?,
    onBack: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleMastered: () -> Unit,
    onSavePersonalNote: (String) -> Unit,
    onQuizUnit: () -> Unit,
    onAskAi: () -> Unit
) {
    BackHandler(onBack = onBack)
    var personalText by remember(note.id, progress?.personalNote) {
        mutableStateOf(progress?.personalNote.orEmpty())
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = unit?.shortTitle ?: "Study Module",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("note_reader_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to notes")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleBookmark) {
                        Icon(
                            imageVector = if (progress?.bookmarked == true) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (progress?.bookmarked == true) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onToggleMastered) {
                        Icon(
                            imageVector = if (progress?.mastered == true) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Mastered",
                            tint = if (progress?.mastered == true) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${note.eraOrTag} • ${note.readMinutes} min study",
                    style = MonoBadgeStyle,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Text(text = note.title, style = MaterialTheme.typography.headlineMedium)
            Text(text = note.summary, style = MaterialTheme.typography.bodyLarge)

            // Key Authors
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Key Authors & Theorists", style = MaterialTheme.typography.titleSmall)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        note.keyAuthors.forEach { author ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = author,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Landmark Texts
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Landmark Texts", style = MaterialTheme.typography.titleSmall)
                    note.keyTexts.forEach { textTitle ->
                        Text("• $textTitle", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // High-Yield Exam Points
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "High-Yield LAQP01 Exam Takeaways",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    note.highYieldPoints.forEachIndexed { idx, point ->
                        Text(
                            text = "${idx + 1}. $point",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (idx < note.highYieldPoints.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            // Personal Annotation (Cloud-synced)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Your Cloud-Synced Annotation", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = personalText,
                        onValueChange = { personalText = it },
                        placeholder = { Text("Add custom dates, NotebookLM page refs, or memory cues...") },
                        minLines = 2,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { onSavePersonalNote(personalText) },
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("Save Annotation")
                    }
                }
            }

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onQuizUnit,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Quiz Unit")
                }
                Button(
                    onClick = onAskAi,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Ask AI Tutor")
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PyqInteractiveCard(
    question: QuizQuestion,
    selectedOption: Int?,
    onSelectOption: (Int) -> Unit,
    onAskAi: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pyq_card_${question.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = question.yearTag ?: "PYQ",
                        style = MonoBadgeStyle,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "Unit ${question.unitId.removePrefix("unit_")}",
                    style = MonoBadgeStyle,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = question.prompt,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )

            question.options.forEachIndexed { idx, opt ->
                val isSelected = selectedOption == idx
                val isCorrect = idx == question.correctIndex
                val revealed = selectedOption != null

                val bgColor = when {
                    revealed && isCorrect -> MaterialTheme.colorScheme.tertiaryContainer
                    revealed && isSelected && !isCorrect -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val borderColor = when {
                    revealed && isCorrect -> MaterialTheme.colorScheme.tertiary
                    revealed && isSelected && !isCorrect -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.outline
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = bgColor,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable { onSelectOption(idx) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "${'A' + idx}.",
                            style = MonoBadgeStyle,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (selectedOption != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        OutlinedButton(
                            onClick = onAskAi,
                            modifier = Modifier.heightIn(min = 48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Explain Deeper with AI")
                        }
                    }
                }
            }
        }
    }
}
