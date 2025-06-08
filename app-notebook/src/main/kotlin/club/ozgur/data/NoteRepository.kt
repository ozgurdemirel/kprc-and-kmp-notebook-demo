package club.ozgur.data

import club.ozgur.client.NoteRpcClient
import club.ozgur.model.CreateNoteRequest
import club.ozgur.model.Note
import club.ozgur.model.UpdateNoteRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RemoteNoteRepository {

    suspend fun addNote(note: Note) = withContext(Dispatchers.IO) {
        val service = NoteRpcClient.getService()
        service.createNote(CreateNoteRequest(note.title, note.content))
    }

    suspend fun updateNote(updatedNote: Note) = withContext(Dispatchers.IO) {
        val service = NoteRpcClient.getService()
        service.updateNote(
            updatedNote.id,
            UpdateNoteRequest(updatedNote.title, updatedNote.content)
        )
    }

    suspend fun deleteNote(noteId: String) = withContext(Dispatchers.IO) {
        val service = NoteRpcClient.getService()
        service.deleteNote(noteId)
    }

    suspend fun getNoteById(noteId: String): Note? = withContext(Dispatchers.IO) {
        val service = NoteRpcClient.getService()
        service.getNoteById(noteId)
    }

    suspend fun getAllNotes(): List<Note> = withContext(Dispatchers.IO) {
        val service = NoteRpcClient.getService()
        service.getAllNotes()
    }
    
    suspend fun getAllNotesFlow(): Flow<List<Note>> = withContext(Dispatchers.IO) {
        val service = NoteRpcClient.getService()
        service.getAllNotesFlow().flowOn(Dispatchers.IO)
    }

}
