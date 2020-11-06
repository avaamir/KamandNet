package mp.amir.ir.kamandnet.respository.apiservice

import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.models.api.Entity
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface WewiClient {
    //"app.wewi.ir"
    @POST(" ")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("loc") location: String,
        @Field("act") action: String,
        @Field("username_type") usernameType: String
    ): Response<Entity<User>>

}

