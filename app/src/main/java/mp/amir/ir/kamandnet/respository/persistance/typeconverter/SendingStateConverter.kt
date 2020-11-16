package mp.amir.ir.kamandnet.respository.persistance.typeconverter

import androidx.room.TypeConverter
import mp.amir.ir.kamandnet.models.enums.SendingState
import mp.amir.ir.kamandnet.utils.general.getEnumById

object SendingStateConverter {
    @JvmStatic
    @TypeConverter
    fun fromModel(sendingState: SendingState?): Int {
        return sendingState?.id ?: 0
    }

    @JvmStatic
    @TypeConverter
    fun toModel(sendingStateId: Int): SendingState {
        return getEnumById(SendingState::id, sendingStateId)
    }

}