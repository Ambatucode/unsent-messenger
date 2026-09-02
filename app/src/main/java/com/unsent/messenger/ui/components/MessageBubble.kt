package com.unsent.messenger.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.unsent.messenger.ui.theme.UnsentRed
import com.unsent.messenger.ui.theme.UnsentRedBg
import com.unsent.messenger.ui.theme.UnsentRedBorder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: MessageEntity,
    onDeleteMessage: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showFullImageDialog by remember { mutableStateOf(false) }

    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

    // Decode image from disk if available
    val imageBitmap = remember(message.mediaFilePath) {
        if (!message.mediaFilePath.isNullOrEmpty()) {
            val file = File(message.mediaFilePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
            } else null
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
                        if (message.isUnsent) Modifier.border(1.dp, UnsentRedBorder, RoundedCornerShape(16.dp))
                        else Modifier
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showMenu = true }
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    // Unsent Tag
                    if (message.isUnsent) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Unsent",
                                tint = UnsentRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MESSAGE UNSENT / RETRACTED BY SENDER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = UnsentRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    // Render Image if available
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Saved photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .heightIn(min = 140.dp, max = 260.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showFullImageDialog = true }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Message Body Text (if not empty or if distinct from photo tag)
                    if (message.messageText.isNotBlank() && (imageBitmap == null || message.messageText != "📷 [Photo]")) {
                        Text(
                            text = message.messageText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (message.isUnsent) Color(0xFF330000) else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (message.isUnsent) FontWeight.Medium else FontWeight.Normal
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Timestamp
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (message.isUnsent) UnsentRed.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
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
                    .background(Color.Black.copy(alpha = 0.92f))
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
