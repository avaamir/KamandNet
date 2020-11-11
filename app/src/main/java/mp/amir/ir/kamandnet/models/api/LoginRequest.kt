package mp.amir.ir.kamandnet.models.api

import com.google.gson.annotations.SerializedName

class LoginRequest(
    @SerializedName("UserName")
    val username: String,
    @SerializedName("Password")
    val password: String
)