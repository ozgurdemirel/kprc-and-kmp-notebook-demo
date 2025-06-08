package club.ozgur.server.repository

import club.ozgur.model.Note
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryNoteRepositoryTest {
    
    @Test
    fun `save should store and return note`() = runTest {
        val repository = InMemoryNoteRepository(maxSize = 10)
        val note = Note(id = "test-1", title = "Test Note", content = "Test Content")
        
        val result = repository.save(note)
        
        assertEquals(note, result)
        assertEquals(note, repository.findById("test-1"))
    }
    
    @Test
    fun `findById should return null for non-existent note`() = runTest {
        val repository = InMemoryNoteRepository()
        
        val result = repository.findById("non-existent")
        
        assertNull(result)
    }
    
    @Test
    fun `findAll should return all stored notes`() = runTest {
        val repository = InMemoryNoteRepository()
        val note1 = Note(id = "1", title = "First")
        val note2 = Note(id = "2", title = "Second")
        
        repository.save(note1)
        repository.save(note2)
        
        val result = repository.findAll()
        
        assertEquals(2, result.size)
        assertTrue(result.contains(note1))
        assertTrue(result.contains(note2))
    }
    
    @Test
    fun `deleteById should remove note and return true`() = runTest {
        val repository = InMemoryNoteRepository()
        val note = Note(id = "test-1", title = "Test")
        
        repository.save(note)
        val result = repository.deleteById("test-1")
        
        assertTrue(result)
        assertNull(repository.findById("test-1"))
    }
    
    @Test
    fun `deleteById should return false for non-existent note`() = runTest {
        val repository = InMemoryNoteRepository()
        
        val result = repository.deleteById("non-existent")
        
        assertFalse(result)
    }
    
    @Test
    fun `deleteAll should remove all notes`() = runTest {
        val repository = InMemoryNoteRepository()
        repository.save(Note(id = "1", title = "First"))
        repository.save(Note(id = "2", title = "Second"))
        
        repository.deleteAll()
        
        assertEquals(0, repository.count())
        assertTrue(repository.findAll().isEmpty())
    }
    
    @Test
    fun `count should return number of stored notes`() = runTest {
        val repository = InMemoryNoteRepository()
        
        assertEquals(0, repository.count())
        
        repository.save(Note(id = "1", title = "First"))
        assertEquals(1, repository.count())
        
        repository.save(Note(id = "2", title = "Second"))
        assertEquals(2, repository.count())
    }
    
    @Test
    fun `existsById should return true if note exists`() = runTest {
        val repository = InMemoryNoteRepository()
        val note = Note(id = "test-1", title = "Test")
        
        repository.save(note)
        
        assertTrue(repository.existsById("test-1"))
        assertFalse(repository.existsById("non-existent"))
    }
    
    @Test
    fun `repository should respect max size limit`() = runTest {
        val repository = InMemoryNoteRepository(maxSize = 2)
        
        repository.save(Note(id = "1", title = "First", lastModified = 1000L))
        repository.save(Note(id = "2", title = "Second", lastModified = 2000L))
        assertEquals(2, repository.count())
        
        repository.save(Note(id = "3", title = "Third", lastModified = 3000L))
        
        assertEquals(2, repository.count())
        assertNull(repository.findById("1"))
        assertNotNull(repository.findById("2"))
        assertNotNull(repository.findById("3"))
    }
    
    @Test
    fun `updating existing note should not trigger size limit`() = runTest {
        val repository = InMemoryNoteRepository(maxSize = 2)
        
        repository.save(Note(id = "1", title = "First", lastModified = 1000L))
        repository.save(Note(id = "2", title = "Second", lastModified = 2000L))
        
        repository.save(Note(id = "1", title = "Updated First", lastModified = 3000L))
        
        assertEquals(2, repository.count())
        assertNotNull(repository.findById("1"))
        assertNotNull(repository.findById("2"))
        assertEquals("Updated First", repository.findById("1")?.title)
    }
} 