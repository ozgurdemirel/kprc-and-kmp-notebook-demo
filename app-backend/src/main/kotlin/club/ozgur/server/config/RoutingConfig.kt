package club.ozgur.server.config

import club.ozgur.server.repository.NoteRepository
import club.ozgur.server.service.NoteServiceImpl
import club.ozgur.service.NoteService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.*
import kotlinx.rpc.krpc.serialization.json.json

fun Application.configureRouting(
    noteRepository: NoteRepository,
    config: AppConfig
) {
    routing {
        rpc(config.rpc.endpoint) {
            rpcConfig {
                serialization {
                    json()
                }
            }
            
            registerService<NoteService> { serviceContext ->
                NoteServiceImpl(
                    repository = noteRepository,
                    coroutineContext = serviceContext
                )
            }
        }
    }
} 