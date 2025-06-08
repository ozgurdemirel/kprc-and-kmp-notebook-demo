package club.ozgur.server

import club.ozgur.server.config.*
import club.ozgur.server.exception.ErrorResponse
import club.ozgur.server.repository.ConcurrentNoteRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.path
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import kotlin.time.Duration.Companion.seconds


fun main() {
    val config = AppConfig.load()
    
    embeddedServer(
        Netty,
        port = config.server.port,
        host = config.server.host,
        watchPaths = if (config.server.developmentMode) listOf("classes") else emptyList()
    ) {
        module(config)
    }.start(wait = true)
}

fun Application.module(config: AppConfig) {
    installFeatures()

    val noteRepository = ConcurrentNoteRepository()
    
    configureRouting(noteRepository, config)
    
    log.info("Server started on ${config.server.host}:${config.server.port}")
}

fun Application.installFeatures() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/api") }
    }
    
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
        header("X-Service", "Note-RPC-Server")
    }
    
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Internal server error", cause.message)
            )
        }
    }
}