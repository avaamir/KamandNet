package mp.amir.ir.kamandnet.models.api

import com.google.gson.annotations.SerializedName
import mp.amir.ir.kamandnet.utils.kamand.Constants

class Entity<T> (
    @SerializedName("entity")
    val entity: T?,
    @SerializedName("isSuccess")
    val isSucceed: Boolean,
    @SerializedName("message")
    private val _message: String?
) {
    val message get() = _message ?: Constants.SERVER_ERROR
}