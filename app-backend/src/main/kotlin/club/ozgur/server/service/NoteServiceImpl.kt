package club.ozgur.server.service

import club.ozgur.model.*
import club.ozgur.server.exception.*
import club.ozgur.server.repository.NoteRepository
import club.ozgur.server.util.IdGenerator
import club.ozgur.server.util.TimeProvider
import club.ozgur.service.NoteService
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import org.slf4j.LoggerFactory

class NoteServiceImpl(
    private val repository: NoteRepository,
    private val idGenerator: IdGenerator = IdGenerator.Default,
    private val timeProvider: TimeProvider = TimeProvider.Default,
    override val coroutineContext: CoroutineContext
) : NoteService, CoroutineScope {
    
    private val logger = LoggerFactory.getLogger(NoteServiceImpl::class.java)
    
    private val _notesFlow = MutableSharedFlow<List<Note>>(replay = 1)
    
    init {
        launch {
            emitCurrentNotes()
        }
    }
    
    override fun getAllNotesFlow(): Flow<List<Note>> = _notesFlow.asSharedFlow()

    private suspend fun emitCurrentNotes() {
        val notes = repository.findAll().sortedByDescending { it.lastModified }
        _notesFlow.emit(notes)
        logger.debug("Emitted ${notes.size} notes to flow")
    }
    
    override suspend fun createNote(request: CreateNoteRequest): Note {
        logger.debug("Creating note with title: ${request.title}")
        
        validateCreateRequest(request)
        
        val note = Note(
            id = idGenerator.generate(),
            title = request.title?.trim(),
            content = request.content?.trim(),
            lastModified = timeProvider.currentTimeMillis()
        )
        
        return repository.save(note).also {
            logger.info("Created note with id: ${it.id}")
            launch { emitCurrentNotes() }
        }
    }
    
    override suspend fun getNoteById(id: String): Note? {
        logger.debug("Getting note by id: $id")
        
        if (id.isBlank()) {
            throw InvalidNoteIdException("Note ID cannot be blank")
        }
        
        return repository.findById(id).also {
            if (it == null) {
                logger.warn("Note not found with id: $id")
            }
        }
    }
    
    override suspend fun getAllNotes(): List<Note> {
        logger.debug("Getting all notes")
        
        return repository.findAll()
            .sortedByDescending { it.lastModified }
            .also { logger.debug("Retrieved ${it.size} notes") }
    }
    
    override suspend fun updateNote(id: String, request: UpdateNoteRequest): Note? {
        logger.debug("Updating note with id: $id")
        
        if (id.isBlank()) {
            throw InvalidNoteIdException("Note ID cannot be blank")
        }
        
        val existingNote = repository.findById(id)
            ?: throw NoteNotFoundException("Note not found with id: $id")
        
        val hasChanges = request.title != null || request.content != null
        if (!hasChanges) {
            logger.debug("No changes detected for note: $id")
            return existingNote
        }
        
        val updatedNote = existingNote.copy(
            title = request.title?.trim() ?: existingNote.title,
            content = request.content?.trim() ?: existingNote.content,
            lastModified = timeProvider.currentTimeMillis()
        )
        
        validateNote(updatedNote)
        
        return repository.save(updatedNote).also {
            logger.info("Updated note with id: $id")
            launch { emitCurrentNotes() }
        }
    }
    
    override suspend fun deleteNote(id: String): Boolean {
        logger.debug("Deleting note with id: $id")
        
        if (id.isBlank()) {
            throw InvalidNoteIdException("Note ID cannot be blank")
        }
        
        return repository.deleteById(id).also { deleted ->
            if (deleted) {
                logger.info("Deleted note with id: $id")
                launch { emitCurrentNotes() }
            } else {
                logger.warn("Failed to delete note with id: $id (not found)")
            }
        }
    }
    
    override suspend fun searchNotes(query: String): List<Note> {
        logger.debug("Searching notes with query: $query")
        
        if (query.isBlank()) {
            return getAllNotes()
        }
        
        val searchTerm = query.trim().lowercase()
        
        return repository.findAll()
            .filter { note ->
                (note.title?.lowercase()?.contains(searchTerm) == true) ||
                (note.content?.lowercase()?.contains(searchTerm) == true)
            }
            .sortedByDescending { it.lastModified }
            .also { logger.debug("Found ${it.size} notes matching query: $query") }
    }
    
    private fun validateCreateRequest(request: CreateNoteRequest) {
        validateTitle(request.title)
        validateContent(request.content)
    }
    
    private fun validateNote(note: Note) {
        validateTitle(note.title)
        validateContent(note.content)
    }
    
    private fun validateTitle(title: String?) {
        if (title.isNullOrBlank()) {
            throw InvalidNoteException("Note title cannot be blank")
        }
        val titleLength = title.length
        if (titleLength > 200) {
            throw InvalidNoteException("Note title cannot exceed 200 characters")
        }
    }
    
    private fun validateContent(content: String?) {
        val contentLength = content?.length ?: 0
        if (contentLength > 10000) {
            throw InvalidNoteException("Note content cannot exceed 10000 characters")
        }
    }
}