package club.ozgur.data

import club.ozgur.client.NoteRpcClient
import club.ozgur.model.CreateNoteRequest
import club.ozgur.model.Note
import club.ozgur.model.UpdateNoteRequest
import club.ozgur.service.NoteService
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RemoteNoteRepositoryTest {

    private val mockNoteService = mockk<NoteService>()
    private val repository = RemoteNoteRepository()

    @Test
    fun `addNote should create note via RPC service`() = runTest {
        val note = Note(id = "test-1", title = "Test Note", content = "Test Content", lastModified = 1000L)
        val expectedRequest = CreateNoteRequest(note.title, note.content)
        val expectedResponse = note.copy(id = "generated-id")

        mockkObject(NoteRpcClient)
        coEvery { NoteRpcClient.getService() } returns mockNoteService
        coEvery { mockNoteService.createNote(expectedRequest) } returns expectedResponse

        val result = repository.addNote(note)

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { mockNoteService.createNote(expectedRequest) }
        
        unmockkObject(NoteRpcClient)
    }

    @Test
    fun `updateNote should update note via RPC service`() = runTest {
        val note = Note(id = "test-1", title = "Updated Note", content = "Updated Content", lastModified = 2000L)
        val expectedRequest = UpdateNoteRequest(note.title, note.content)
        val expectedResponse = note

        mockkObject(NoteRpcClient)
        coEvery { NoteRpcClient.getService() } returns mockNoteService
        coEvery { mockNoteService.updateNote(note.id, expectedRequest) } returns expectedResponse

        val result = repository.updateNote(note)

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { mockNoteService.updateNote(note.id, expectedRequest) }
        
        unmockkObject(NoteRpcClient)
    }

    @Test
    fun `deleteNote should delete note via RPC service`() = runTest {
        val noteId = "test-1"

        mockkObject(NoteRpcClient)
        coEvery { NoteRpcClient.getService() } returns mockNoteService
        coEvery { mockNoteService.deleteNote(noteId) } returns true

        val result = repository.deleteNote(noteId)

        assertTrue(result)
        coVerify(exactly = 1) { mockNoteService.deleteNote(noteId) }
        
        unmockkObject(NoteRpcClient)
    }

    @Test
    fun `getNoteById should return note if exists`() = runTest {
        val noteId = "test-1"
        val expectedNote = Note(id = noteId, title = "Test Note", content = "Test Content", lastModified = 1000L)

        mockkObject(NoteRpcClient)
        coEvery { NoteRpcClient.getService() } returns mockNoteService
        coEvery { mockNoteService.getNoteById(noteId) } returns expectedNote

        val result = repository.getNoteById(noteId)

        assertEquals(expectedNote, result)
        coVerify(exactly = 1) { mockNoteService.getNoteById(noteId) }
        
        unmockkObject(NoteRpcClient)
    }

    @Test
    fun `getNoteById should return null if not exists`() = runTest {
        val noteId = "non-existent"

        mockkObject(NoteRpcClient)
        coEvery { NoteRpcClient.getService() } returns mockNoteService
        coEvery { mockNoteService.getNoteById(noteId) } returns null

        val result = repository.getNoteById(noteId)

        assertNull(result)
        coVerify(exactly = 1) { mockNoteService.getNoteById(noteId) }
        
        unmockkObject(NoteRpcClient)
    }

    @Test
    fun `getAllNotes should return all notes`() = runTest {
        val expectedNotes = listOf(
            Note(id = "1", title = "First Note", content = "First Content", lastModified = 1000L),
            Note(id = "2", title = "Second Note", content = "Second Content", lastModified = 2000L)
        )

        mockkObject(NoteRpcClient)
        coEvery { NoteRpcClient.getService() } returns mockNoteService
        coEvery { mockNoteService.getAllNotes() } returns expectedNotes

        val result = repository.getAllNotes()

        assertEquals(expectedNotes, result)
        coVerify(exactly = 1) { mockNoteService.getAllNotes() }
        
        unmockkObject(NoteRpcClient)
    }

    @Test
    fun `getAllNotesFlow should return notes flow`() = runTest {
        val expectedNotes = listOf(
            Note(id = "1", title = "First Note", content = "First Content", lastModified = 1000L),
            Note(id = "2", title = "Second Note", content = "Second Content", lastModified = 2000L)
        )
        val expectedFlow = flowOf(expectedNotes)

        mockkObject(NoteRpcClient)
        coEvery { NoteRpcClient.getService() } returns mockNoteService
        coEvery { mockNoteService.getAllNotesFlow() } returns expectedFlow

        val resultFlow = repository.getAllNotesFlow()

        resultFlow.collect { notes ->
            assertEquals(expectedNotes, notes)
        }
        coVerify(exactly = 1) { mockNoteService.getAllNotesFlow() }
        
        unmockkObject(NoteRpcClient)
    }
} 