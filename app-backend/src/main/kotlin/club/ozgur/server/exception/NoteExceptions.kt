package club.ozgur.server.exception

import kotlinx.serialization.Serializable

sealed class NoteException(message: String) : Exception(message)

class NoteNotFoundException(message: String) : NoteException(message)
class InvalidNoteException(message: String) : NoteException(message)
class InvalidNoteIdException(message: String) : NoteException(message)
class NotePersistenceException(message: String, cause: Throwable? = null) : NoteException(message)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String?,
    val timestamp: Long = System.currentTimeMillis()
) 