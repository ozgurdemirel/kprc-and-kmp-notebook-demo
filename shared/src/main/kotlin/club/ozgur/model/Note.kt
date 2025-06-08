package club.ozgur.model

import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String? = null,
    val content: String? = null,
    val lastModified: Long = System.currentTimeMillis()
)

@Serializable
data class CreateNoteRequest(
    val title: String? = null,
    val content: String? = null
)

@Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val content: String? = null
)