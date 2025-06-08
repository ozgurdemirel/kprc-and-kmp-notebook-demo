package club.ozgur.server.service

import club.ozgur.model.CreateNoteRequest
import club.ozgur.model.Note
import club.ozgur.model.UpdateNoteRequest
import club.ozgur.server.exception.InvalidNoteException
import club.ozgur.server.exception.InvalidNoteIdException
import club.ozgur.server.exception.NoteNotFoundException
import club.ozgur.server.repository.InMemoryNoteRepository
import club.ozgur.server.util.IdGenerator
import club.ozgur.server.util.TimeProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NoteServiceImplTest {

    private val repository = InMemoryNoteRepository()
    private val idGenerator = IdGenerator.Sequential("test")
    private val timeProvider = TimeProvider.Fixed(1000L)
    private val service = NoteServiceImpl(
        repository = repository,
        idGenerator = idGenerator,
        timeProvider = timeProvider,
        coroutineContext = EmptyCoroutineContext
    )

    @Test
    fun `createNote should create and return note`() = runTest {
        val request = CreateNoteRequest(title = "Test Note", content = "Test Content")

        val result = service.createNote(request)

        assertNotNull(result.id)
        assertEquals("Test Note", result.title)
        assertEquals("Test Content", result.content)
        assertEquals(1000L, result.lastModified)
    }

    @Test
    fun `createNote should fail with blank title`() = runTest {
        val request = CreateNoteRequest(title = "", content = "Test Content")

        assertFailsWith<InvalidNoteException> {
            service.createNote(request)
        }
    }

    @Test
    fun `createNote should fail with null title`() = runTest {
        val request = CreateNoteRequest(title = null, content = "Test Content")

        assertFailsWith<InvalidNoteException> {
            service.createNote(request)
        }
    }

    @Test
    fun `getNoteById should return note if exists`() = runTest {
        val note = Note(id = "test-1", title = "Test", content = "Content", lastModified = 1000L)
        repository.save(note)

        val result = service.getNoteById("test-1")

        assertEquals(note, result)
    }

    @Test
    fun `getNoteById should return null if not exists`() = runTest {
        val result = service.getNoteById("non-existent")

        assertNull(result)
    }

    @Test
    fun `getNoteById should fail with blank id`() = runTest {
        assertFailsWith<InvalidNoteIdException> {
            service.getNoteById("")
        }
    }

    @Test
    fun `getAllNotes should return notes sorted by lastModified`() = runTest {
        val note1 = Note(id = "1", title = "First", lastModified = 1000L)
        val note2 = Note(id = "2", title = "Second", lastModified = 2000L)
        repository.save(note1)
        repository.save(note2)

        val result = service.getAllNotes()

        assertEquals(2, result.size)
        assertEquals("Second", result[0].title)
        assertEquals("First", result[1].title)
    }

    @Test
    fun `updateNote should update existing note`() = runTest {
        val originalNote = Note(id = "test-1", title = "Original", content = "Original Content", lastModified = 1000L)
        repository.save(originalNote)

        val updateRequest = UpdateNoteRequest(title = "Updated", content = "Updated Content")
        val result = service.updateNote("test-1", updateRequest)

        assertNotNull(result)
        assertEquals("Updated", result.title)
        assertEquals("Updated Content", result.content)
        assertEquals(1000L, result.lastModified)
    }

    @Test
    fun `updateNote should fail if note not found`() = runTest {
        val updateRequest = UpdateNoteRequest(title = "Updated")

        assertFailsWith<NoteNotFoundException> {
            service.updateNote("non-existent", updateRequest)
        }
    }

    @Test
    fun `deleteNote should delete existing note`() = runTest {
        val note = Note(id = "test-1", title = "Test")
        repository.save(note)

        val result = service.deleteNote("test-1")

        assertTrue(result)
        assertNull(repository.findById("test-1"))
    }

    @Test
    fun `searchNotes should find notes by title and content`() = runTest {
        val note1 = Note(id = "1", title = "Kotlin Programming", content = "Learning Kotlin")
        val note2 = Note(id = "2", title = "Java Development", content = "Working with Java")
        val note3 = Note(id = "3", title = "Programming Languages", content = "Comparison of languages")

        repository.save(note1)
        repository.save(note2)
        repository.save(note3)

        val result = service.searchNotes("Programming")

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Kotlin Programming" })
        assertTrue(result.any { it.title == "Programming Languages" })
    }
} 