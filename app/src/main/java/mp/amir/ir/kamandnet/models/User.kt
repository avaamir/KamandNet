package mp.amir.ir.kamandnet.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "user_tb")
data class User(
    @PrimaryKey
    @SerializedName("personId")
    var id: String,
    @SerializedName("name")
    var name: String?,
    @SerializedName("token")
    var token: String,
    @SerializedName("profilePic")
    var profilePic: String?,
)