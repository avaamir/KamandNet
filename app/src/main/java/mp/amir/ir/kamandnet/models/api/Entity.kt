package mp.amir.ir.kamandnet.models.api

import com.google.gson.annotations.SerializedName
import mp.amir.ir.kamandnet.utils.wewi.Constants

class Entity<T> (
    @SerializedName("entity")
    val entity: T?,
    @SerializedName("isSucceed")
    val isSucceed: Boolean,
    @SerializedName("msg")
    private val _message: String?
) {
    val message get() = _message ?: Constants.SERVER_ERROR
}