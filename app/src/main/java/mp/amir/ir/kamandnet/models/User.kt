package mp.amir.ir.kamandnet.models

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("fullName")
    val name: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("profilePic")
    val profilePic: String?,
)