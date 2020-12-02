package mp.amir.ir.kamandnet.respository.apiservice

import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.UpdateResponse
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.models.api.Entity
import mp.amir.ir.kamandnet.models.api.LoginRequest
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

/** tamami api ha(masir , method, requestBody, ResponseBody) dar in ja moshakhas mishavad*/

interface KamandClient {
    @POST("User/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Entity<User>>


    @GET("repairRequest")
    suspend fun getInstructions(): Response<Entity<List<Instruction>>>

    @POST("AppVersion/FindLastAppVersion")
    suspend fun checkUpdates(): Response<Entity<UpdateResponse>>

    @POST("RepairRequest/AddRepairLog")
    @Multipart
    suspend fun submitInstructions(
        @Part("RequestId") id: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("tagCode") tagCode: RequestBody?,
        @Part("finishDate") doneDate: RequestBody,
        @Part images: List<MultipartBody.Part>?
    ): Response<Entity<Any>>

    @POST("ء")
    suspend fun submitInstructions2(
        @Body instructionSubmitBody: RequestBody
    ): Response<Response<Entity<Any>>>
}

/*

fun createBody(id: Int, files: List<File>): RequestBody {
    */
/*
//Another way to upload files
@POST("exampleendpoint/{id}")
fun uploadDocuments(@Path("id") id: String, @Body body: RequestBody): Response<Unit>
*//*


    return MultipartBody.Builder().setType(MultipartBody.FORM).apply {
        addFormDataPart("type", "booking")
        addFormDataPart("user", "username")
        addFormDataPart("message", "message text goes here")
        addFormDataPart("contact_number", "0123456789")
        addFormDataPart("contact_email", "email@address.com")
        // my files are List<ByteArray>, okhttp has a few utility methods like .toRequestBody for various types like below
        files.forEach { file ->
            val bytes = file.readBytes()
            addFormDataPart(
                "files[]",
                file.name,
                bytes.toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, bytes.size)
            )
        }
    }.build()
}

*/
