package map.mine.audiologs.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
     val firstName: String,
     val lastName: String,
     val email: String,
     val username: String,
     val password: String
)