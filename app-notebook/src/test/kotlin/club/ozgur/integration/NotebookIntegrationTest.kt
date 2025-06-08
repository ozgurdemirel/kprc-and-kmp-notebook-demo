package club.ozgur.integration

import club.ozgur.model.Note
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NotebookIntegrationTest {

    @Test
    fun `Note model should work correctly`() {
        val note = Note(
            id = "test-1",
            title = "Integration Test Note",
            content = "This is a test note for integration testing",
            lastModified = System.currentTimeMillis()
        )

        assertNotNull(note.id)
        assertEquals("Integration Test Note", note.title)
        assertEquals("This is a test note for integration testing", note.content)
        assertTrue(note.lastModified > 0)
    }

    @Test
    fun `Note copy should work correctly`() {
        val originalNote = Note(
            id = "original",
            title = "Original Title",
            content = "Original Content",
            lastModified = 1000L
        )

        val copiedNote = originalNote.copy(
            title = "Updated Title",
            lastModified = 2000L
        )

        assertEquals("original", copiedNote.id)
        assertEquals("Updated Title", copiedNote.title)
        assertEquals("Original Content", copiedNote.content)
        assertEquals(2000L, copiedNote.lastModified)
    }

    @Test
    fun `Note with null values should work correctly`() {
        val note = Note(
            id = "test-null",
            title = null,
            content = null,
            lastModified = 0L
        )

        assertEquals("test-null", note.id)
        assertEquals(null, note.title)
        assertEquals(null, note.content)
        assertEquals(0L, note.lastModified)
    }

    @Test
    fun `coroutines test framework should work`() = runTest {
        var testValue = false
        
        kotlinx.coroutines.delay(1)
        testValue = true
        
        assertTrue(testValue)
    }
} 