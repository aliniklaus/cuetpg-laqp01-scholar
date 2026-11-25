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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.QuestionMarkState
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoTimerStyle
import com.example.viewmodel.ExamSessionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPlayerScreen(
    session: ExamSessionState,
    onSelectOption: (Int) -> Unit,
    onClearResponse: () -> Unit,
    onToggleMarkForReview: () -> Unit,
    onGoToQuestion: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onExit: () -> Unit,
    onAskAiForQuestion: (String) -> Unit
) {
    var showPaletteDialog by remember { mutableStateOf(false) }
    var showSubmitConfirm by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }

    BackHandler {
        if (session.isSubmitted) onExit() else showExitConfirm = true
    }

    if (session.isSubmitted) {
        ExamResultsScreen(
            session = session,
            onExit = onExit,
            onAskAiForQuestion = onAskAiForQuestion
        )
        return
    }

    val currentQ = session.currentQuestion
    val selectedOption = session.selectedAnswers[session.currentIndex]
    val isMarked = session.currentIndex in session.markedForReview
    val timerDisplay = remember(session.isTimedCountdown, session.remainingSeconds, session.elapsedSeconds) {
        val sec = if (session.isTimedCountdown) session.remainingSeconds else session.elapsedSeconds
        val mins = sec / 60
        val rem = sec % 60
        "%02d:%02d".format(mins, rem)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Q ${session.currentIndex + 1} / ${session.questions.size}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { showExitConfirm = true },
                        modifier = Modifier.testTag("exam_exit_button")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Exit exam")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (session.isTimedCountdown && session.remainingSeconds < 300) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    ) {
                        Text(
                            text = timerDisplay,
                            style = MonoTimerStyle,
                            color = if (session.isTimedCountdown && session.remainingSeconds < 300) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    IconButton(
                        onClick = { showPaletteDialog = true },
                        modifier = Modifier.testTag("exam_palette_button")
                    ) {
                        Icon(Icons.Filled.GridView, contentDescription = "Open question palette")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = session.currentIndex > 0,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                    }
                    OutlinedButton(
                        onClick = onToggleMarkForReview,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) {
                        Icon(
                            imageVector = if (isMarked) Icons.Filled.Flag else Icons.Outlined.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(if (isMarked) "Marked" else "Review")
                    }
                    if (session.currentIndex < session.questions.lastIndex) {
                        Button(
                            onClick = onNext,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("exam_next_button")
                        ) {
                            Text("Next")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Button(
                            onClick = { showSubmitConfirm = true },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("exam_submit_button")
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (currentQ != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "UNIT ${currentQ.unitId.removePrefix("unit_")}",
                            style = MonoBadgeStyle,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "+4 / -1 Marking",
                        style = MonoBadgeStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = currentQ.prompt,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(18.dp)
                    )
                }

                currentQ.options.forEachIndexed { idx, optText ->
                    val isSelected = selectedOption == idx
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp)
                            .clickable { onSelectOption(idx) }
                            .testTag("exam_option_$idx")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "${'A' + idx}",
                                    style = MonoBadgeStyle,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Text(
                                text = optText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onClearResponse,
                        enabled = selectedOption != null
                    ) {
                        Text("Clear Selection")
                    }
                    TextButton(onClick = { showSubmitConfirm = true }) {
                        Text("Finish & Submit")
                    }
                }
            }
        }
    }

    if (showPaletteDialog) {
        QuestionPaletteDialog(
            session = session,
            onDismiss = { showPaletteDialog = false },
            onSelectQuestion = { idx ->
                onGoToQuestion(idx)
                showPaletteDialog = false
            }
        )
    }

    if (showSubmitConfirm) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirm = false },
            title = { Text("Submit Test?") },
            text = {
                Text(
                    "Answered: ${session.selectedAnswers.size} / ${session.questions.size}\n" +
                        "Marked for Review: ${session.markedForReview.size}\n" +
                        "Unattempted: ${session.unattemptedCount}\n\n" +
                        "Your score will be calculated with +4/-1 marking and synced to your cloud history."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirm = false
                        onSubmit()
                    },
                    modifier = Modifier.testTag("confirm_submit_exam_button")
                ) {
                    Text("Submit Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirm = false }) {
                    Text("Keep Practicing")
                }
            }
        )
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text("Exit Test Session?") },
            text = { Text("Your current answers will be discarded if you leave without submitting.") },
            confirmButton = {
                Button(onClick = {
                    showExitConfirm = false
                    onExit()
                }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text("Resume")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestionPaletteDialog(
    session: ExamSessionState,
    onDismiss: () -> Unit,
    onSelectQuestion: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Question Palette (${session.selectedAnswers.size}/${session.questions.size} answered)") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    session.questions.indices.forEach { idx ->
                        val markState = session.markStateFor(idx)
                        val containerColor = when (markState) {
                            QuestionMarkState.ANSWERED -> MaterialTheme.colorScheme.tertiaryContainer
                            QuestionMarkState.MARKED_REVIEW -> MaterialTheme.colorScheme.secondaryContainer
                            QuestionMarkState.ANSWERED_AND_MARKED -> MaterialTheme.colorScheme.primaryContainer
                            QuestionMarkState.UNVISITED -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = containerColor,
                            border = if (idx == session.currentIndex) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else null,
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { onSelectQuestion(idx) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MonoBadgeStyle
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamResultsScreen(
    session: ExamSessionState,
    onExit: () -> Unit,
    onAskAiForQuestion: (String) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Test Scorecard & Solutions") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Done")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(session.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${session.score} / ${session.maxScore}",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("✓ ${session.correctCount} Correct (+${session.correctCount * 4})", style = MaterialTheme.typography.labelLarge)
                            Text("✗ ${session.wrongCount} Wrong (-${session.wrongCount})", style = MaterialTheme.typography.labelLarge)
                            Text("○ ${session.unattemptedCount} Skipped", style = MaterialTheme.typography.labelLarge)
                        }
                        if (session.resultSaved) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Saved to cloud history", style = MaterialTheme.typography.bodySmall)
                            }
                        } else if (session.saveError != null) {
                            Text(
                                text = "Sync note: ${session.saveError}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Button(
                            onClick = onExit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                        ) {
                            Text("Back to Tests")
                        }
                    }
                }
            }

            item {
                Text("Question-by-Question Review", style = MaterialTheme.typography.titleLarge)
            }

            itemsIndexed(session.questions) { idx, q ->
                val userChoice = session.selectedAnswers[idx]
                val isCorrect = userChoice == q.correctIndex
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        when {
                            userChoice == null -> MaterialTheme.colorScheme.outline
                            isCorrect -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Q${idx + 1} • Unit ${q.unitId.removePrefix("unit_")} • " +
                                when {
                                    userChoice == null -> "Skipped (0)"
                                    isCorrect -> "Correct (+4)"
                                    else -> "Incorrect (-1)"
                                },
                            style = MonoBadgeStyle,
                            color = when {
                                userChoice == null -> MaterialTheme.colorScheme.onSurfaceVariant
                                isCorrect -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.secondary
                            }
                        )
                        Text(q.prompt, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                        if (userChoice != null && !isCorrect) {
                            Text(
                                text = "Your answer: ${q.options[userChoice]}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Text(
                            text = "Correct answer: ${q.options[q.correctIndex]}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = q.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = {
                                onAskAiForQuestion(
                                    "Explain this CUET PG LAQP01 question and why '${q.options[q.correctIndex]}' is correct:\n\n${q.prompt}"
                                )
                            },
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Ask AI to Explain")
                        }
                    }
                }
            }
        }
    }
}
