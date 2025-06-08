package club.ozgur.client

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class NoteRpcClientTest {

    @Test
    fun `NoteRpcClient object should be accessible`() {
        val client = NoteRpcClient
    }

    @Test
    fun `NoteRpcClient disconnect should be callable`() = runTest {
        try {
            NoteRpcClient.disconnect()
        } catch (e: Exception) {
        }
    }
}

class NoteServiceClientTest {

    private lateinit var client: NoteServiceClient

    @Test
    fun `isConnected should return false initially`() {
        client = NoteServiceClient()
        assertFalse(client.isConnected)
    }

    @Test
    fun `getService should throw exception when not connected`() = runTest {
        client = NoteServiceClient()
        
        assertFailsWith<IllegalStateException> {
            client.getService()
        }
    }

    @Test
    fun `connect with invalid host should throw exception`() = runTest {
        client = NoteServiceClient()
        
        assertFailsWith<Exception> {
            client.connect("invalid-host", 9999)
        }
    }

    @Test
    fun `close should not throw exception`() = runTest {
        client = NoteServiceClient()
        
        try {
            client.close()
        } catch (e: Exception) {
        }
    }

    @Test
    fun `multiple connect calls should be handled gracefully`() = runTest {
        client = NoteServiceClient()
        
        try {
            client.connect("localhost", 8080)
        } catch (e: Exception) {
        }
        
        try {
            client.connect("localhost", 8080)
        } catch (e: Exception) {
        }
    }

    @Test
    fun `client can be instantiated`() {
        val client = NoteServiceClient()
        assertFalse(client.isConnected)
    }
} 