package com.unsent.messenger.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.unsent.messenger.data.MessageEntity
import com.unsent.messenger.ui.theme.MessengerBlue
import com.unsent.messenger.ui.theme.MessengerGradientEnd
import com.unsent.messenger.ui.theme.MessengerGradientStart
import com.unsent.messenger.ui.theme.UnsentRed
import com.unsent.messenger.ui.theme.UnsentRedBg
import com.unsent.messenger.ui.theme.UnsentRedBorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: MessageEntity,
    onDeleteMessage: (Long) -> Unit,
    onToggleUnsent: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showMenu by remember { mutableStateOf(false) }
    var showFullImageDialog by remember { mutableStateOf(false) }

    // On-Demand Reveal State
    var isRevealed by remember(message.id) { mutableStateOf(false) }
    var isLoadingMedia by remember { mutableStateOf(false) }

    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

    val hasMedia = !message.mediaFilePath.isNullOrEmpty()
    val mediaFile = remember(message.mediaFilePath) {
        if (hasMedia) File(message.mediaFilePath!!) else null
    }

    // Decode bitmap on-demand only when revealed
    val imageBitmap = remember(isRevealed, message.mediaFilePath) {
        if (isRevealed && mediaFile != null && mediaFile.exists()) {
            BitmapFactory.decodeFile(mediaFile.absolutePath)?.asImageBitmap()
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Sender name
        Text(
            text = message.senderName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (message.isUnsent) UnsentRed else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
        )

        Box {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                    .background(
                        if (message.isUnsent) UnsentRedBg else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .then(
                        if (message.isUnsent) Modifier.border(1.5.dp, UnsentRedBorder, RoundedCornerShape(16.dp))
                        else Modifier
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showMenu = true }
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    // Unsent Tag Banner
                    if (message.isUnsent) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Unsent",
                                tint = UnsentRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "⚠️ MESSAGE UNSENT / DELETED BY SENDER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = UnsentRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // 📸 ON-DEMAND PHOTO PLACEHOLDER & REVEAL
                    if (hasMedia) {
                        if (!isRevealed) {
                            // Sleek placeholder card asking user to reveal
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (message.isUnsent) UnsentRed.copy(alpha = 0.08f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        1.dp,
                                        if (message.isUnsent) UnsentRedBorder.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (!isLoadingMedia) {
                                            isLoadingMedia = true
                                            scope.launch {
                                                delay(250) // smooth transition
                                                isLoadingMedia = false
                                                isRevealed = true
                                            }
                                        }
                                    }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (message.isUnsent) UnsentRed.copy(alpha = 0.15f)
                                                else MessengerBlue.copy(alpha = 0.12f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isLoadingMedia) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(22.dp),
                                                strokeWidth = 2.dp,
                                                color = if (message.isUnsent) UnsentRed else MessengerBlue
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.CloudDownload,
                                                contentDescription = "Download Photo",
                                                tint = if (message.isUnsent) UnsentRed else MessengerBlue,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (message.isUnsent) "Unsent Photo Captured" else "Photo Message",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (message.isUnsent) UnsentRed else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Tap to download & reveal",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        } else {
                            // Render Revealed Image
                            if (imageBitmap != null) {
                                Image(
                                    bitmap = imageBitmap,
                                    contentDescription = "Revealed photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .heightIn(min = 140.dp, max = 260.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { showFullImageDialog = true }
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }

                    // Message Body Text
                    if (message.messageText.isNotBlank() && (!hasMedia || message.messageText != "📷 [Photo]")) {
                        Text(
                            text = message.messageText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (message.isUnsent) Color(0xFF4A0E0C) else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (message.isUnsent) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Timestamp
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (message.isUnsent) UnsentRed.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            // Long-press Context Menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (message.messageText.isNotBlank()) {
                    DropdownMenuItem(
                        text = { Text("Copy Text") },
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Messenger Text", message.messageText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            showMenu = false
                        }
                    )
                }

                if (hasMedia && isRevealed) {
                    DropdownMenuItem(
                        text = { Text("Hide / Re-lock Photo") },
                        leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null) },
                        onClick = {
                            isRevealed = false
                            showMenu = false
                        }
                    )
                }

                DropdownMenuItem(
                    text = { Text(if (message.isUnsent) "Unmark as Unsent" else "Mark as Unsent") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (message.isUnsent) Icons.Default.BookmarkBorder else Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = UnsentRed
                        )
                    },
                    onClick = {
                        onToggleUnsent(message.id, !message.isUnsent)
                        showMenu = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Delete This Message", color = UnsentRed) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = UnsentRed) },
                    onClick = {
                        onDeleteMessage(message.id)
                        showMenu = false
                    }
                )
            }
        }
    }

    // Full Screen Photo Viewer Dialog
    if (showFullImageDialog && imageBitmap != null) {
        Dialog(
            onDismissRequest = { showFullImageDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.94f))
                    .clickable { showFullImageDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Full view photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                IconButton(
                    onClick = { showFullImageDialog = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}
