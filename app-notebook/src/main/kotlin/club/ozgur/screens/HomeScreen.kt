package club.ozgur.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import club.ozgur.data.RemoteNoteRepository
import club.ozgur.model.Note
import kotlinx.coroutines.launch
import ui.screens.NoteEditorScreen
import java.text.SimpleDateFormat
import java.util.*

object HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        
        val repository = remember { RemoteNoteRepository() }
        
        LaunchedEffect(Unit) {
            try {
                isLoading = true
                error = null
                repository.getAllNotesFlow().collect { notesList ->
                    notes = notesList
                    isLoading = false
                }
            } catch (e: Exception) {
                error = e.message ?: "Unknown error occurred"
                isLoading = false
            }
        }

        val scope = rememberCoroutineScope()
        
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("My Notebook") })
            },
            floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navigator.push(NoteEditorScreen(noteId = null))
                        },
                        backgroundColor = MaterialTheme.colors.primary
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add New Note")
                    }
            }
        ) { paddingValues ->
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $error", color = MaterialTheme.colors.error)
                    }
                }
                else -> {
                    Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                        if (notes.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No notes yet. Click the '+' button to add one.")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(notes, key = { it.id }) { note ->
                                    NoteCardItem(
                                        note = note,
                                        onClick = {
                                            navigator.push(NoteEditorScreen(noteId = note.id))
                                        },
                                        onDelete = {
                                            scope.launch {
                                                try {
                                                    note.id.let { noteId ->
                                                        repository.deleteNote(noteId)
                                                    }
                                                } catch (e: Exception) {
                                                    error = e.message ?: "Failed to delete note"
                                                }
                                            }
                                        }
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NoteCardItem(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    var isHovered by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false }
            .clickable(onClick = onClick),
        elevation = if (isHovered) 8.dp else 4.dp,
        backgroundColor = if (isHovered) MaterialTheme.colors.primary.copy(alpha = 0.05f) else MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                note.title?.let { Text(it, style = MaterialTheme.typography.h6) }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    (note.content?.take(100) ?: "") + if ((note.content?.length ?: 0) > 100) "..." else "",
                    style = MaterialTheme.typography.body2,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Last Modified: ${formatTimestamp(note.lastModified)}",
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Note", tint = Color.Gray)
            }

        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}