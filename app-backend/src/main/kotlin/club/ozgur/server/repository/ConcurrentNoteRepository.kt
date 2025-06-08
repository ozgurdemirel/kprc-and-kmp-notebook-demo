package club.ozgur.server.repository

import club.ozgur.model.Note
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class ConcurrentNoteRepository : NoteRepository {
    private val notes = ConcurrentHashMap<String, Note>()
    private val mutex = Mutex()
    
    override suspend fun save(note: Note): Note = mutex.withLock {
        notes[note.id] = note
        note
    }
    
    override suspend fun findById(id: String): Note? {
        return notes[id]
    }
    
    override suspend fun findAll(): List<Note> {
        return notes.values.toList()
    }
    
    override suspend fun deleteById(id: String): Boolean = mutex.withLock {
        notes.remove(id) != null
    }
    
    override suspend fun deleteAll() = mutex.withLock {
        notes.clear()
    }
    
    override suspend fun count(): Long {
        return notes.size.toLong()
    }
    
    override suspend fun existsById(id: String): Boolean {
        return notes.containsKey(id)
    }
} 