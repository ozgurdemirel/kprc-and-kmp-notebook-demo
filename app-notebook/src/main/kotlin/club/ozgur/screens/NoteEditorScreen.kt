package ui.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import club.ozgur.data.RemoteNoteRepository
import club.ozgur.model.Note
import kotlinx.coroutines.launch

data class NoteEditorScreen(val noteId: String? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository = remember { RemoteNoteRepository() }
        val scope = rememberCoroutineScope()
        
        var editingNote by remember { mutableStateOf<Note?>(null) }
        var isLoading by remember { mutableStateOf(noteId != null) }
        
        LaunchedEffect(noteId) {
            if (noteId != null) {
                try {
                    isLoading = true
                    editingNote = repository.getNoteById(noteId)
                } catch (e: Exception) {
                    
                } finally {
                    isLoading = false
                }
            }
        }

        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var contentTextSize by remember { mutableStateOf(16f) }

        LaunchedEffect(editingNote) {
            editingNote?.let { note ->
                title = note.title ?: ""
                content = note.content ?: ""
            }
        }

        val focusRequester = remember { FocusRequester() }

        val saveNoteAction: () -> Unit = {
            if (title.isNotBlank() || content.isNotBlank()) {
                scope.launch {
                    try {
                        val noteToSave = editingNote?.copy(
                            title = title.ifBlank { "Untitled Note" },
                            content = content
                        ) ?: Note(
                            title = title.ifBlank { "Untitled Note" },
                            content = content
                        )

                        if (editingNote == null) {
                            repository.addNote(noteToSave)
                        } else {
                            repository.updateNote(noteToSave)
                        }
                        navigator.pop()
                    } catch (e: Exception) {
                        
                    }
                }
            } else {
                navigator.pop()
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(if (editingNote == null) "Create New Note" else "Edit Note") },
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Go Back")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = saveNoteAction,
                        backgroundColor = MaterialTheme.colors.primary
                    ) {
                        Icon(Icons.Filled.Done, contentDescription = "Save Note")
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                        .focusable()
                        .onKeyEvent { keyEvent ->
                            if ((keyEvent.isCtrlPressed || keyEvent.isMetaPressed) &&
                                keyEvent.key == Key.S &&
                                keyEvent.type == KeyEventType.KeyDown
                            ) {
                                saveNoteAction()
                                true
                            } else {
                                false
                            }
                        }
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Content Text Size: ", style = MaterialTheme.typography.body2)
                        Text("${contentTextSize.toInt()} sp", style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = contentTextSize,
                        onValueChange = { contentTextSize = it },
                        valueRange = 12f..32f,
                        steps = 19,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Note Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        textStyle = TextStyle(fontSize = contentTextSize.sp, lineHeight = (contentTextSize * 1.5).sp),
                    )
                }
                
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}