package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.model.AiStudyMode
import com.example.model.QuizQuestion
import com.example.model.SavedAiNote
import com.example.model.UiState
import com.example.ui.theme.MonoBadgeStyle
import com.example.viewmodel.AiMessage
import com.example.viewmodel.AiTutorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    uiState: AiTutorUiState,
    savedNotesState: UiState<List<SavedAiNote>>,
    onSelectMode: (AiStudyMode) -> Unit,
    onInputChange: (String) -> Unit,
    onSendPrompt: () -> Unit,
    onAskPreset: (AiStudyMode, String) -> Unit,
    onSaveMessageCard: (AiMessage, String) -> Unit,
    onDeleteSavedCard: (String) -> Unit,
    onLaunchGeneratedQuiz: (String, List<QuizQuestion>) -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Chat, 1 = Saved Cards
    val savedCards = (savedNotesState as? UiState.Success)?.data.orEmpty()
    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(uiState.messages.size, uiState.streamingText.length) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header + Tab Switcher + Mode Chips
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "LAQP01 AI Study Assistant",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        modifier = Modifier.testTag("ai_tab_tutor")
                    ) {
                        Text("Study Tutor")
                    }
                    SegmentedButton(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        modifier = Modifier.testTag("ai_tab_saved")
                    ) {
                        Text("Saved Cards (${savedCards.size})")
                    }
                }

                if (activeTab == 0) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AiStudyMode.entries) { mode ->
                            FilterChip(
                                selected = uiState.selectedMode == mode,
                                onClick = { onSelectMode(mode) },
                                label = { Text(mode.label) },
                                modifier = Modifier.testTag("ai_mode_${mode.wireValue.lowercase()}")
                            )
                        }
                    }
                    Text(
                        text = uiState.selectedMode.shortHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (activeTab == 0) {
            // Conversation / Starter Area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.messages.isEmpty() && !uiState.isLoading) {
                    item {
                        AiStarterPromptsCard(
                            selectedMode = uiState.selectedMode,
                            onSelectStarter = { mode, prompt -> onAskPreset(mode, prompt) }
                        )
                    }
                }

                items(uiState.messages, key = { it.id }) { msg ->
                    val topicHint = remember(uiState.messages, msg.id) {
                        val idx = uiState.messages.indexOfFirst { it.id == msg.id }
                        if (idx > 0 && uiState.messages[idx - 1].isUser) {
                            uiState.messages[idx - 1].text.take(80)
                        } else {
                            "LAQP01 ${msg.mode.label}"
                        }
                    }
                    AiMessageCard(
                        message = msg,
                        onSaveCard = { onSaveMessageCard(msg, topicHint) },
                        onCopy = {
                            clipboard.setText(AnnotatedString(msg.text))
                            Toast.makeText(context, "Copied response", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (uiState.isLoading) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Text(
                                        text = if (uiState.selectedMode == AiStudyMode.QUIZ_GEN) {
                                            "Generating structured LAQP01 MCQs..."
                                        } else {
                                            "LAQP01 Tutor is writing..."
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (uiState.streamingText.isNotBlank()) {
                                    Text(
                                        text = uiState.streamingText,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                val quizReady = uiState.generatedQuiz
                if (quizReady != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = quizReady.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "${quizReady.questions.size} custom MCQs ready with +4 / -1 scoring and explanations.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Button(
                                    onClick = { onLaunchGeneratedQuiz(quizReady.title, quizReady.questions) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .testTag("launch_generated_quiz_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Launch Generated Quiz")
                                }
                            }
                        }
                    }
                }

                if (uiState.errorMessage != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.inputText,
                        onValueChange = onInputChange,
                        placeholder = {
                            Text(
                                when (uiState.selectedMode) {
                                    AiStudyMode.EXPLAIN -> "Ask about any PYQ, theorist, or text..."
                                    AiStudyMode.QUIZ_GEN -> "Topic or paste study notes for 5 MCQs..."
                                    AiStudyMode.MNEMONIC -> "List authors, terms, or works to memorize..."
                                    AiStudyMode.TIMELINE -> "Period, movement, or literary tradition..."
                                    AiStudyMode.COMPARISON -> "e.g., Eliot vs Brooks, or Said vs Bhabha..."
                                }
                            )
                        },
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_prompt_input")
                    )
                    Button(
                        onClick = onSendPrompt,
                        enabled = uiState.inputText.isNotBlank() && !uiState.isLoading,
                        modifier = Modifier
                            .heightIn(min = 52.dp)
                            .testTag("ai_send_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send prompt")
                    }
                }
            }
        } else {
            // Saved AI Study Cards
            if (savedCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tap 'Save Card' on any AI Tutor explanation, mnemonic, timeline, or comparison to sync it here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedCards, key = { it.id }) { card ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
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
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = card.mode,
                                                style = MonoBadgeStyle,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = card.unitTag,
                                                style = MonoBadgeStyle,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            clipboard.setText(AnnotatedString(card.content))
                                            Toast.makeText(context, "Study card copied", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy")
                                        }
                                        IconButton(onClick = { onDeleteSavedCard(card.id) }) {
                                            Icon(
                                                Icons.Filled.DeleteOutline,
                                                contentDescription = "Delete card",
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                                Text(card.topic, style = MaterialTheme.typography.titleMedium)
                                Text(card.content, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiStarterPromptsCard(
    selectedMode: AiStudyMode,
    onSelectStarter: (AiStudyMode, String) -> Unit
) {
    val starters = listOf(
        AiStudyMode.EXPLAIN to "Explain T.S. Eliot's 'Dissociation of Sensibility' and 'Objective Correlative' with likely LAQP01 MCQ traps.",
        AiStudyMode.QUIZ_GEN to "Unit 6 Literary Theory: Russian Formalism, Structuralism, Deconstruction & Postcolonialism",
        AiStudyMode.MNEMONIC to "Create mnemonics to remember the University Wits, Pre-Raphaelites, and the Booker of Bookers winners.",
        AiStudyMode.TIMELINE to "Chronological timeline of Indian English Fiction from Rajmohan's Wife (1864) to Tomb of Sand (2022).",
        AiStudyMode.COMPARISON to "Compare New Criticism vs Russian Formalism vs Deconstruction for CUET PG LAQP01."
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Try a Quick LAQP01 Study Prompt (${selectedMode.label})",
                style = MaterialTheme.typography.titleMedium
            )
            starters.forEach { (mode, prompt) ->
                Surface(
                    onClick = { onSelectStarter(mode, prompt) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = mode.label.uppercase(),
                            style = MonoBadgeStyle,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = prompt, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AiMessageCard(
    message: AiMessage,
    onSaveCard: () -> Unit,
    onCopy: () -> Unit
) {
    val container = if (message.isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (message.isUser) "YOU • ${message.mode.label.uppercase()}" else "AI TUTOR • ${message.mode.label.uppercase()}",
                    style = MonoBadgeStyle,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!message.isUser) {
                    Row {
                        IconButton(onClick = onCopy) {
                            Icon(
                                Icons.Filled.ContentCopy,
                                contentDescription = "Copy response",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium
            )

            if (!message.isUser && message.mode != AiStudyMode.QUIZ_GEN) {
                OutlinedButton(
                    onClick = onSaveCard,
                    enabled = !message.isSaved,
                    modifier = Modifier.heightIn(min = 44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (message.isSaved) Icons.Filled.BookmarkAdded else Icons.Filled.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (message.isSaved) "Saved to Study Cards" else "Save Study Card")
                }
            }
        }
    }
}
