package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.NoteProgress
import com.example.model.NotebookLink
import com.example.model.StudyNote
import com.example.model.SyllabusUnit
import com.example.model.TestAttempt
import com.example.model.UiState
import com.example.ui.theme.MonoBadgeStyle
import kotlin.math.roundToInt

private data class FocusAreaRecommendation(
    val unit: SyllabusUnit,
    val accuracyPercent: Int?,
    val masteredCount: Int,
    val totalCount: Int,
    val reason: String
)

@Composable
fun HomeScreen(
    userDisplayName: String,
    units: List<SyllabusUnit>,
    allNotes: List<StudyNote>,
    noteProgressState: UiState<List<NoteProgress>>,
    attemptsState: UiState<List<TestAttempt>>,
    notebookLinksState: UiState<List<NotebookLink>>,
    onStartFullMock: () -> Unit,
    onOpenNotebookVault: () -> Unit,
    onOpenUnitNotes: (String) -> Unit,
    onDrillUnit: (SyllabusUnit) -> Unit,
    onOpenAiTutor: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current

    val progressList = (noteProgressState as? UiState.Success)?.data.orEmpty()
    val attemptsList = (attemptsState as? UiState.Success)?.data.orEmpty()
    val linksList = (notebookLinksState as? UiState.Success)?.data.orEmpty()

    val masteredIds = remember(progressList) {
        progressList.filter { it.mastered }.map { it.noteId }.toSet()
    }
    val bookmarkedCount = remember(progressList) {
        progressList.count { it.bookmarked }
    }
    val masteryFraction = remember(masteredIds, allNotes) {
        if (allNotes.isEmpty()) 0f else masteredIds.size.toFloat() / allNotes.size.toFloat()
    }
    val bestMockScore = remember(attemptsList) {
        attemptsList.filter { it.mode == "MOCK" }.maxOfOrNull { it.score }
    }

    // Parse per-unit accuracy averages from unitAccuracySummary (e.g., "U1: 82% • U6: 40%")
    val unitAccuracyAverages = remember(attemptsList) {
        val buckets = mutableMapOf<Int, MutableList<Int>>()
        val regex = Regex("""U(\d+):\s*(\d+)%""")
        attemptsList.forEach { attempt ->
            val summary = attempt.unitAccuracySummary.orEmpty()
            regex.findAll(summary).forEach { match ->
                val unitNum = match.groupValues[1].toIntOrNull()
                val pct = match.groupValues[2].toIntOrNull()
                if (unitNum != null && pct != null) {
                    buckets.getOrPut(unitNum) { mutableListOf() }.add(pct)
                }
            }
        }
        buckets.mapValues { (_, list) -> list.average().roundToInt() }
    }

    // Chronological percentage trend points (oldest -> newest, up to 10 recent tests)
    val trendPoints = remember(attemptsList) {
        attemptsList.take(10).reversed().map { attempt ->
            val max = attempt.maxScore.coerceAtLeast(1)
            ((attempt.score.toFloat() / max.toFloat()) * 100f).coerceIn(0f, 100f)
        }
    }

    // Focus area recommendations driven by mock/quiz accuracy + unmastered syllabus modules
    val focusRecommendations = remember(units, allNotes, masteredIds, unitAccuracyAverages) {
        units.map { unit ->
            val unitNotes = allNotes.filter { it.unitId == unit.id }
            val mastered = unitNotes.count { it.id in masteredIds }
            val accuracy = unitAccuracyAverages[unit.number]
            val priorityScore = when {
                accuracy != null -> accuracy - ((unitNotes.size - mastered) * 8)
                else -> 65 - ((unitNotes.size - mastered) * 10)
            }
            val reason = when {
                accuracy != null && accuracy < 60 ->
                    "Mock/quiz accuracy is $accuracy% — revise core concepts and re-drill."
                accuracy != null && mastered < unitNotes.size ->
                    "$accuracy% test accuracy with ${unitNotes.size - mastered} module(s) left to master."
                mastered < unitNotes.size ->
                    "${unitNotes.size - mastered} of ${unitNotes.size} modules unmastered — build baseline coverage."
                else ->
                    "Strong mastery (${mastered}/${unitNotes.size}) — keep sharp with timed drills."
            }
            Triple(
                priorityScore,
                FocusAreaRecommendation(unit, accuracy, mastered, unitNotes.size, reason),
                Unit
            )
        }
            .sortedBy { it.first }
            .take(3)
            .map { it.second }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CUET PG • LAQP01 DASHBOARD",
                        style = MonoBadgeStyle,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Welcome, ${userDisplayName.ifBlank { "Scholar" }}",
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onSignOut,
                    modifier = Modifier.testTag("sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Sign out"
                    )
                }
            }
        }

        // Readiness Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Overall LAQP01 Readiness",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${masteredIds.size} of ${allNotes.size} modules mastered • $bookmarkedCount bookmarked",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "${(masteryFraction * 100).toInt()}%",
                                style = MonoBadgeStyle,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { masteryFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatChip(
                            label = "Best Mock",
                            value = if (bestMockScore != null) "$bestMockScore / 300" else "Not taken",
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "Tests Logged",
                            value = "${attemptsList.size}",
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "NotebookLM",
                            value = "${linksList.size} links",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onStartFullMock,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("home_start_mock_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("75-Q Mock")
                        }
                        OutlinedButton(
                            onClick = onOpenAiTutor,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("home_open_ai_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("AI Tutor")
                        }
                    }
                }
            }
        }

        // Quiz Performance Trends Card (Canvas Chart)
        item {
            QuizPerformanceTrendCard(
                trendPoints = trendPoints,
                attempts = attemptsList,
                onStartMock = onStartFullMock
            )
        }

        // Suggested Focus Areas Card
        item {
            Text(
                text = "Suggested Focus Areas",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(focusRecommendations, key = { "focus_${it.unit.id}" }) { rec ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (rec.accuracyPercent != null && rec.accuracyPercent < 60) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_card_${rec.unit.id}")
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "UNIT ${rec.unit.number}",
                                style = MonoBadgeStyle,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (rec.accuracyPercent != null && rec.accuracyPercent < 60) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            }
                        ) {
                            Text(
                                text = if (rec.accuracyPercent != null) {
                                    "Accuracy ${rec.accuracyPercent}%"
                                } else {
                                    "${rec.masteredCount}/${rec.totalCount} mastered"
                                },
                                style = MonoBadgeStyle,
                                color = if (rec.accuracyPercent != null && rec.accuracyPercent < 60) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(text = rec.unit.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = rec.reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onOpenUnitNotes(rec.unit.id) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Revise Notes")
                        }
                        Button(
                            onClick = { onDrillUnit(rec.unit) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Drill Unit")
                        }
                    }
                }
            }
        }

        // Subject-Wise Syllabus Progress Matrix (All 7 Units)
        item {
            Text(
                text = "Subject Progress (7 LAQP01 Units)",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(units, key = { it.id }) { unit ->
            val unitNotes = allNotes.filter { it.unitId == unit.id }
            val unitMastered = unitNotes.count { it.id in masteredIds }
            val unitFrac = if (unitNotes.isEmpty()) 0f else unitMastered.toFloat() / unitNotes.size.toFloat()
            val accuracy = unitAccuracyAverages[unit.number]

            Card(
                onClick = { onOpenUnitNotes(unit.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_unit_card_${unit.id}")
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
                            text = "UNIT ${unit.number} • ${unit.title}",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (unitMastered == unitNotes.size && unitNotes.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Unit Mastered",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (accuracy != null) {
                            Text(
                                text = "$accuracy% acc",
                                style = MonoBadgeStyle,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = { unitFrac },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "$unitMastered / ${unitNotes.size} modules mastered" +
                            if (accuracy != null) " • Avg quiz accuracy: $accuracy%" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // NotebookLM Bookmark Vault Preview
        item {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bookmarks,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Google NotebookLM Vault",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        TextButton(
                            onClick = onOpenNotebookVault,
                            modifier = Modifier.testTag("open_notebook_vault_button")
                        ) {
                            Text("Manage (${linksList.size})")
                        }
                    }

                    if (linksList.isEmpty()) {
                        Text(
                            text = "Keep links to your NotebookLM study notebooks organized by LAQP01 unit for instant review.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = onOpenNotebookVault,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("home_add_notebook_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Open NotebookLM Vault")
                        }
                    } else {
                        linksList.take(2).forEach { link ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link.url)))
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "Unable to open URL", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = link.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = link.unitTag,
                                            style = MonoBadgeStyle,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = "Launch NotebookLM",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizPerformanceTrendCard(
    trendPoints: List<Float>,
    attempts: List<TestAttempt>,
    onStartMock: () -> Unit
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val fillStartColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
    val fillEndColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.0f)
    val gridColor = MaterialTheme.colorScheme.outline
    val dotCenterColor = MaterialTheme.colorScheme.surface

    val avgPct = remember(trendPoints) {
        if (trendPoints.isEmpty()) 0 else trendPoints.average().roundToInt()
    }
    val latestPct = trendPoints.lastOrNull()?.roundToInt() ?: 0

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_trend_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Quiz Performance Trend", style = MaterialTheme.typography.titleMedium)
                }
                if (trendPoints.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Avg $avgPct% • Last $latestPct%",
                            style = MonoBadgeStyle,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (trendPoints.isEmpty()) {
                Text(
                    text = "Take a practice drill or 75-Q mock test to plot your score trajectory across sessions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(
                    onClick = onStartMock,
                    modifier = Modifier.heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Take First Assessment")
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(top = 6.dp, bottom = 4.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val padV = 12.dp.toPx()
                        val padH = 10.dp.toPx()
                        val plotW = (w - padH * 2).coerceAtLeast(1f)
                        val plotH = (h - padV * 2).coerceAtLeast(1f)

                        // Horizontal reference grid lines (0%, 50%, 100%)
                        listOf(0f, 0.5f, 1f).forEach { level ->
                            val y = padV + plotH * (1f - level)
                            drawLine(
                                color = gridColor,
                                start = Offset(padH, y),
                                end = Offset(w - padH, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        val coords = if (trendPoints.size == 1) {
                            val y = padV + plotH * (1f - (trendPoints[0] / 100f))
                            listOf(Offset(w / 2f, y))
                        } else {
                            trendPoints.mapIndexed { idx, pct ->
                                val x = padH + (idx.toFloat() / trendPoints.lastIndex.toFloat()) * plotW
                                val y = padV + plotH * (1f - (pct / 100f))
                                Offset(x, y)
                            }
                        }

                        if (coords.size > 1) {
                            val linePath = Path().apply {
                                moveTo(coords.first().x, coords.first().y)
                                for (i in 1 until coords.size) {
                                    lineTo(coords[i].x, coords[i].y)
                                }
                            }
                            val fillPath = Path().apply {
                                addPath(linePath)
                                lineTo(coords.last().x, padV + plotH)
                                lineTo(coords.first().x, padV + plotH)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(fillStartColor, fillEndColor),
                                    startY = padV,
                                    endY = padV + plotH
                                )
                            )
                            drawPath(
                                path = linePath,
                                color = lineColor,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        coords.forEach { pt ->
                            drawCircle(color = lineColor, radius = 6.dp.toPx(), center = pt)
                            drawCircle(color = dotCenterColor, radius = 3.dp.toPx(), center = pt)
                        }
                    }
                }

                val latestAttempt = attempts.firstOrNull()
                if (latestAttempt != null) {
                    Text(
                        text = "Latest: ${latestAttempt.title} (${latestAttempt.score}/${latestAttempt.maxScore})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
