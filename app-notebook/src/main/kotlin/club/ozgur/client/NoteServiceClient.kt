package club.ozgur.client

import club.ozgur.service.NoteService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import java.util.concurrent.atomic.AtomicBoolean

object NoteRpcClient {
    private val client = NoteServiceClient()
    private val _isConnected = AtomicBoolean(false)
    private val connectionMutex = Mutex()

    val isConnected: Boolean get() = _isConnected.get()

    suspend fun connect(host: String = "localhost", port: Int = 8080) {
        if (_isConnected.get() && client.isConnected) {
            return
        }

        connectionMutex.withLock {
            if (client.isConnected) {
                _isConnected.set(true)
                return@withLock
            }

            _isConnected.set(false)
            try {
                client.connect(host, port)
                _isConnected.set(true)
            } catch (e: Exception) {
                _isConnected.set(false)
                throw e
            }
        }
    }

    suspend fun getService(): NoteService {
        if (!_isConnected.get()) {
            connect()
        }
        return client.getService()
    }

    suspend fun disconnect() {
        if (_isConnected.compareAndSet(true, false)) {
            try {
                client.close()
            } catch (e: Exception) {
                println("Exception during client.close(): ${e.message}")
            }
        }
    }
}

class NoteServiceClient {
    private var httpClient: HttpClient? = null
    private var rpcClient: RpcClient? = null
    private var noteService: NoteService? = null
    private val _isConnected = AtomicBoolean(false)

    val isConnected: Boolean get() = _isConnected.get()

    suspend fun connect(host: String = "localhost", port: Int = 8080) {
        if (_isConnected.get()) {
            return
        }

        var tempHttpClient: HttpClient? = null
        try {
            tempHttpClient = HttpClient(CIO) {
                install(WebSockets)
                installKrpc {
                    waitForServices = true
                }
            }

            val rpc: KtorRpcClient = tempHttpClient.rpc {
                url("ws://$host:$port/api/notes")
                rpcConfig {
                    serialization { json() }
                }
            }

            val service = rpc.withService<NoteService>()

            httpClient = tempHttpClient
            rpcClient = rpc
            noteService = service
            _isConnected.set(true)
        } catch (e: Exception) {
            tempHttpClient?.close()
            cleanupInternal(didConnectSuccessfully = false)
            throw e
        }
    }

    suspend fun getService(): NoteService {
        return noteService ?: throw IllegalStateException("Client not connected or service not initialized. Call connect() first.")
    }

    suspend fun close() {
        if (_isConnected.compareAndSet(true, false)) {
            cleanupInternal(didConnectSuccessfully = true)
        }
    }

    private suspend fun cleanupInternal(didConnectSuccessfully: Boolean) {
        noteService = null

        rpcClient = null

        httpClient?.close()
        httpClient = null
    }
}