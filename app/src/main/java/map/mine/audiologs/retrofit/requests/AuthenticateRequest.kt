package map.mine.audiologs.retrofit.requests

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AuthenticateRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)