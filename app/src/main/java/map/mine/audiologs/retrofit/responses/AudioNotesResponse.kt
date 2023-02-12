package map.mine.audiologs.retrofit.responses

@kotlinx.serialization.Serializable
data class AudioNotesResponse(val name: String?, val url: String?, val description: String?,
                              val size: Long?)