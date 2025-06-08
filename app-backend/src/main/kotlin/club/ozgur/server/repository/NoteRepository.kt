package club.ozgur.server.repository

import club.ozgur.model.Note

interface NoteRepository {
    suspend fun save(note: Note): Note
    suspend fun findById(id: String): Note?
    suspend fun findAll(): List<Note>
    suspend fun deleteById(id: String): Boolean
    suspend fun deleteAll()
    suspend fun count(): Long
    suspend fun existsById(id: String): Boolean
} 