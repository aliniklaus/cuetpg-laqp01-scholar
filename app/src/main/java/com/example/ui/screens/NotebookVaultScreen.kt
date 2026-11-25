package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.NotebookLink
import com.example.model.UiState
import com.example.ui.theme.MonoBadgeStyle

private val UNIT_TAG_OPTIONS = listOf(
    "All Units", "Unit 1", "Unit 2", "Unit 3", "Unit 4", "Unit 5", "Unit 6", "Unit 7"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookVaultScreen(
    linksState: UiState<List<NotebookLink>>,
    onBack: () -> Unit,
    onAddLink: (title: String, url: String, unitTag: String, description: String?, pinned: Boolean) -> Unit,
    onUpdateLink: (id: String, title: String, url: String, unitTag: String, description: String?, pinned: Boolean) -> Unit,
    onTogglePin: (NotebookLink) -> Unit,
    onDeleteLink: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var selectedFilter by remember { mutableStateOf("All") }
    var editingLink by remember { mutableStateOf<NotebookLink?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("NotebookLM Vault", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Organize & launch your Google NotebookLM URLs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("vault_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_notebook_fab")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add NotebookLM link")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterItems = listOf("All") + UNIT_TAG_OPTIONS
                items(filterItems) { tag ->
                    FilterChip(
                        selected = selectedFilter == tag,
                        onClick = { selectedFilter = tag },
                        label = { Text(tag) },
                        modifier = Modifier.testTag("vault_filter_${tag.lowercase().replace(" ", "_")}")
                    )
                }
            }

            when (linksState) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = linksState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                is UiState.Success -> {
                    val filtered = remember(linksState.data, selectedFilter) {
                        if (selectedFilter == "All") linksState.data
                        else linksState.data.filter { it.unitTag == selectedFilter }
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Bookmarks,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Text(
                                        text = "No NotebookLM links saved yet",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Save links to your Google NotebookLM notebooks (e.g., Literary Theory PDFs or Indian Writing notes) to launch them quickly by unit.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Button(
                                        onClick = { showAddDialog = true },
                                        modifier = Modifier.testTag("empty_add_notebook_button")
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Add NotebookLM Link")
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered, key = { it.id }) { link ->
                                NotebookLinkCard(
                                    link = link,
                                    onLaunch = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            Toast.makeText(
                                                context,
                                                "No browser found to open URL",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onCopy = {
                                        clipboard.setText(AnnotatedString(link.url))
                                        Toast.makeText(context, "Notebook URL copied", Toast.LENGTH_SHORT).show()
                                    },
                                    onTogglePin = { onTogglePin(link) },
                                    onEdit = { editingLink = link },
                                    onDelete = { onDeleteLink(link.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        NotebookLinkEditorDialog(
            initial = null,
            onDismiss = { showAddDialog = false },
            onSave = { title, url, unitTag, desc, pinned ->
                onAddLink(title, url, unitTag, desc, pinned)
                showAddDialog = false
            }
        )
    }

    val currentEdit = editingLink
    if (currentEdit != null) {
        NotebookLinkEditorDialog(
            initial = currentEdit,
            onDismiss = { editingLink = null },
            onSave = { title, url, unitTag, desc, pinned ->
                onUpdateLink(currentEdit.id, title, url, unitTag, desc, pinned)
                editingLink = null
            }
        )
    }
}

@Composable
fun NotebookLinkCard(
    link: NotebookLink,
    onLaunch: () -> Unit,
    onCopy: () -> Unit,
    onTogglePin: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.dp,
            color = if (link.pinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notebook_link_card_${link.id}")
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = link.unitTag,
                        style = MonoBadgeStyle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Row {
                    IconButton(onClick = onTogglePin) {
                        Icon(
                            imageVector = if (link.pinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (link.pinned) "Unpin notebook" else "Pin notebook",
                            tint = if (link.pinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit notebook link"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Delete notebook link",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Text(
                text = link.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!link.description.isNullOrBlank()) {
                Text(
                    text = link.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = link.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLaunch,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("launch_notebook_${link.id}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Open NotebookLM")
                }
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy URL",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotebookLinkEditorDialog(
    initial: NotebookLink?,
    onDismiss: () -> Unit,
    onSave: (title: String, url: String, unitTag: String, description: String?, pinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title.orEmpty()) }
    var url by remember { mutableStateOf(initial?.url ?: "https://notebooklm.google.com/") }
    var unitTag by remember { mutableStateOf(initial?.unitTag ?: "All Units") }
    var description by remember { mutableStateOf(initial?.description.orEmpty()) }
    var pinned by remember { mutableStateOf(initial?.pinned ?: false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initial == null) "Add NotebookLM Link" else "Edit NotebookLM Link",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; validationError = null },
                    label = { Text("Notebook Title") },
                    placeholder = { Text("e.g., Unit 6 Postcolonial Theory") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notebook_title_input")
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it; validationError = null },
                    label = { Text("NotebookLM URL (https://)") },
                    placeholder = { Text("https://notebooklm.google.com/notebook/...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notebook_url_input")
                )
                Text(
                    text = "Syllabus Unit Tag",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(UNIT_TAG_OPTIONS) { tag ->
                        FilterChip(
                            selected = unitTag == tag,
                            onClick = { unitTag = tag },
                            label = { Text(tag) }
                        )
                    }
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes / Sources (optional)") },
                    placeholder = { Text("e.g., Said, Spivak, Bhabha summaries") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pin to Home Dashboard", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = pinned, onCheckedChange = { pinned = it })
                }
                if (validationError != null) {
                    Text(
                        text = validationError.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanTitle = title.trim()
                    val cleanUrl = url.trim()
                    when {
                        cleanTitle.isEmpty() -> validationError = "Please enter a notebook title."
                        !cleanUrl.startsWith("https://") || cleanUrl.length < 12 ->
                            validationError = "Please enter a valid https:// NotebookLM URL."
                        else -> onSave(
                            cleanTitle,
                            cleanUrl,
                            unitTag,
                            description.trim().ifEmpty { null },
                            pinned
                        )
                    }
                },
                modifier = Modifier.testTag("save_notebook_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
