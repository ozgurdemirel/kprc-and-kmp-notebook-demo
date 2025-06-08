package club.ozgur.screens

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomeScreenUtilsTest {

    @Test
    fun `formatTimestamp should format timestamp correctly`() {
        val timestamp = 1704110445000L
        
        val result = formatTimestamp(timestamp)
        
        assertTrue(result.contains("/"))
        assertTrue(result.contains(":"))
        assertTrue(result.length >= 10)
    }

    @Test
    fun `formatTimestamp should handle zero timestamp`() {
        val timestamp = 0L
        
        val result = formatTimestamp(timestamp)
        
        assertTrue(result.contains("01/01/1970") || result.contains("1970"))
    }

    @Test
    fun `formatTimestamp should handle current time`() {
        val currentTime = System.currentTimeMillis()
        
        val result = formatTimestamp(currentTime)
        
        assertTrue(result.contains("/"))
        assertTrue(result.contains(":"))
        assertTrue(result.isNotBlank())
    }

    @Test
    fun `formatTimestamp should handle future timestamp`() {
        val futureTimestamp = 1893456000000L
        
        val result = formatTimestamp(futureTimestamp)
        
        assertTrue(result.contains("2030"))
    }

    @Test
    fun `formatTimestamp should be consistent for same timestamp`() {
        val timestamp = 1704110445000L
        
        val result1 = formatTimestamp(timestamp)
        val result2 = formatTimestamp(timestamp)
        
        assertEquals(result1, result2)
    }
} 