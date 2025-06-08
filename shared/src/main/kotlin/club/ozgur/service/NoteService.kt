package club.ozgur.service

import club.ozgur.model.CreateNoteRequest
import club.ozgur.model.Note
import club.ozgur.model.UpdateNoteRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc

@Rpc
interface NoteService : RemoteService {
    suspend fun createNote(request: CreateNoteRequest): Note
    suspend fun getNoteById(id: String): Note?
    suspend fun getAllNotes(): List<Note>
    suspend fun updateNote(id: String, request: UpdateNoteRequest): Note?
    suspend fun deleteNote(id: String): Boolean
    suspend fun searchNotes(query: String): List<Note>
    
    fun getAllNotesFlow(): Flow<List<Note>>
}