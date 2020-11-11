package mp.amir.ir.kamandnet.respository.apiservice

import mp.amir.ir.kamandnet.models.UpdateResponse
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.models.api.Entity
import mp.amir.ir.kamandnet.models.api.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface KamandClient {
    @POST("User/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Entity<User>>

    @POST("AppVersion/FindLastAppVersion")
    suspend fun checkUpdates(): Response<Entity<UpdateResponse>>

}

