package map.mine.audiologs.retrofit.responses

@kotlinx.serialization.Serializable
data class RegisterUserResponse(val username: String?, val password: String?, val firstName: String?,
                                val lastName: String?, val email: String?)