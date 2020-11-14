package mp.amir.ir.kamandnet.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import mp.amir.ir.kamandnet.models.api.InstructionState
import mp.amir.ir.kamandnet.models.api.RepairType
import mp.amir.ir.kamandnet.models.api.TagType
import mp.amir.ir.kamandnet.utils.general.getEnumById

@Entity(tableName = "instructions")
@Parcelize
data class Instruction(
    @PrimaryKey
    @SerializedName("requestId")
    val id: Int,
    @SerializedName("repairTypeTitle")
    val repairTypeTitle: String, //em, pm ..
    @SerializedName("operationTypeTitle")
    val jobType: String, //service, tamir va ..
    @SerializedName("timeToDo")
    val date: String, //zaman ejra
    @SerializedName("requsetState")
    val stateTitle: String,
    @SerializedName("equipmentTreeTitle")
    val nodeType: String,
    @SerializedName("equipmentTitle")
    val nodeInstance: String,

    @SerializedName("requsetStateId")
    val _requestStateId: Int, //1:Started, 2:Done, 3:Confirmed, 4:BarghastKhorde, 5:Cancelled, 6:Tamdid_Dore
    @SerializedName("tagCode")
    val tagCode: String,
    @SerializedName("tagTypeId")
    val _tagTypeId: Int, //1:None, 2:QR, 3:NFC

    @SerializedName("repairTypeId")
    val _repairTypeId: Int, //1:PM 2:EM

    @SerializedName("repairGroupTitle")
    val repairGroupTitle: String,

    ) : Parcelable {
    val name get() = "$jobType $nodeType"

    val repairType get() = getEnumById(RepairType::id, _repairTypeId)

    val tagType
        get() = getEnumById(TagType::id, _tagTypeId)

    val state: InstructionState
        get() = getEnumById(InstructionState::id, _requestStateId)

}