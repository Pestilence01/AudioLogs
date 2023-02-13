package map.mine.audiologs.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AudioNote(
    val path: String,
    @SerialName("note_name") val name: String,
    @SerialName("note_description") val description: String,
    @SerialName("note_size") val size: Long,
    @SerialName("url") var url: String
)