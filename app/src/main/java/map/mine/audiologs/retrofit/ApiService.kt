package map.mine.audiologs.retrofit

import map.mine.audiologs.models.User
import map.mine.audiologs.retrofit.requests.AuthenticateRequest
import map.mine.audiologs.retrofit.responses.AudioNotesResponse
import map.mine.audiologs.retrofit.responses.AuthenticateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/registerUser")
    fun registerUser(@Body request: User): Call<User>

    @POST("/authenticate")
    fun authenticateUser(@Body request: AuthenticateRequest): Call<AuthenticateResponse>

    @Multipart
    @POST("/upload")
    fun uploadAudioNote(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<Message>

    @GET("/files")
    fun getUserAudioNotes(@Header("Authorization") token: String): Call<List<AudioNotesResponse>>

    @GET("/files/{fileId}")
    fun getAudioNote(
        @Header("Authorization") token: String,
        @Path("fileId") fileUrl: String
    ): Call<ResponseBody>

    @DELETE("/files/{fileId}")
    fun deleteAudioNote(
        @Header("Authorization") token: String, @Path("fileId") fileUrl: String
    ): Call<Message>
}