package map.mine.audiologs.retrofit.requests

@kotlinx.serialization.Serializable
data class RegisterUserRequest(val username: String?, val password: String?, val firstName: String?,
                               val lastName: String?, val email: String?)