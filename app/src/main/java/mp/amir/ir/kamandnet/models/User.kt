package mp.amir.ir.kamandnet.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("fullName")
    val name: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("profilePic")
    val profilePic: String?,
)