package com.unsent.messenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unsent.messenger.ui.components.MessageBubble
import com.unsent.messenger.ui.theme.MessengerBlue
import com.unsent.messenger.ui.theme.MessengerGradientEnd
import com.unsent.messenger.ui.theme.MessengerGradientStart
import com.unsent.messenger.ui.theme.UnsentRed
import com.unsent.messenger.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversationId: String,
    title: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val messagesFlow = remember(conversationId) {
        viewModel.getMessagesForConversation(conversationId)
    }
    val messages by messagesFlow.collectAsState()

    var showOnlyUnsent by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    val filteredMessages = remember(messages, showOnlyUnsent) {
        if (showOnlyUnsent) messages.filter { it.isUnsent } else messages
    }

    val listState = rememberLazyListState()

    LaunchedEffect(filteredMessages.size) {
        if (filteredMessages.isNotEmpty()) {
            listState.animateScrollToItem(filteredMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(MessengerGradientStart, MessengerGradientEnd))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title.firstOrNull()?.uppercase() ?: "?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val unsentCount = messages.count { it.isUnsent }
                            Text(
                                text = if (unsentCount > 0) "$unsentCount unsent / ${messages.size} total" else "${messages.size} messages logged",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (unsentCount > 0) UnsentRed else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }

                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete Entire Conversation", color = UnsentRed) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = UnsentRed) },
                            onClick = {
                                showOptionsMenu = false
                                showDeleteConfirmDialog = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chip row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !showOnlyUnsent,
                    onClick = { showOnlyUnsent = false },
                    label = { Text("All (${messages.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MessengerBlue.copy(alpha = 0.15f),
                        selectedLabelColor = MessengerBlue
                    )
                )

                val unsentTotal = messages.count { it.isUnsent }
                FilterChip(
                    selected = showOnlyUnsent,
                    onClick = { showOnlyUnsent = true },
                    label = { Text("Only Unsent ($unsentTotal)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UnsentRed.copy(alpha = 0.15f),
                        selectedLabelColor = UnsentRed
                    )
                )
            }

            if (filteredMessages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showOnlyUnsent) "No unsent messages in this conversation yet." else "No messages recorded.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(
                        items = filteredMessages,
                        key = { it.id }
                    ) { msg ->
                        MessageBubble(
                            message = msg,
                            onDeleteMessage = { id ->
                                viewModel.deleteMessage(id, conversationId)
                            },
                            onToggleUnsent = { id, isUnsent ->
                                viewModel.toggleMessageUnsent(id, isUnsent, conversationId)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Conversation") },
            text = { Text("Are you sure you want to delete all saved messages for '$title'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteConversation(conversationId)
                        showDeleteConfirmDialog = false
                        onBack()
                    }
                ) {
                    Text("Delete", color = UnsentRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
