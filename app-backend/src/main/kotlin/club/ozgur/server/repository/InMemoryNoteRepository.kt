package club.ozgur.server.repository

import club.ozgur.model.Note
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryNoteRepository(
    private val maxSize: Int = 1000
) : NoteRepository {
    private val notes = mutableMapOf<String, Note>()
    private val mutex = Mutex()
    
    override suspend fun save(note: Note): Note = mutex.withLock {
        if (notes.size >= maxSize && !notes.containsKey(note.id)) {
            val oldestNote = notes.values.minByOrNull { it.lastModified }
            oldestNote?.let { notes.remove(it.id) }
        }
        
        notes[note.id] = note
        note
    }
    
    override suspend fun findById(id: String): Note? = mutex.withLock {
        notes[id]
    }
    
    override suspend fun findAll(): List<Note> = mutex.withLock {
        notes.values.toList()
    }
    
    override suspend fun deleteById(id: String): Boolean = mutex.withLock {
        notes.remove(id) != null
    }
    
    override suspend fun deleteAll() = mutex.withLock {
        notes.clear()
    }
    
    override suspend fun count(): Long = mutex.withLock {
        notes.size.toLong()
    }
    
    override suspend fun existsById(id: String): Boolean = mutex.withLock {
        notes.containsKey(id)
    }
} 