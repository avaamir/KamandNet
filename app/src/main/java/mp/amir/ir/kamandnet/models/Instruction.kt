package mp.amir.ir.kamandnet.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

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
    val name get() = "$jobType-$nodeInstance"

     val repairType get() = when(_repairTypeId) { //Shart gozri baraye emal icon em ya pm, sort kardan
         1 -> RepairType.PM
         2 -> RepairType.EM
         else -> throw Exception("new RepairType added and should add to enum: $_repairTypeId")
     }

     val tagType get() = when(_tagTypeId) {
         1 -> TagType.None
         2 -> TagType.QR
         3 -> TagType.NFC
         else -> throw Exception("new Tag Type Added: $_tagTypeId")
     }

     val state: InstructionState get() = when(_requestStateId) {
         1 -> InstructionState.Started
         5 -> InstructionState.Cancelled
         3 -> InstructionState.Confirmed
         2 -> InstructionState.Done
         4 -> InstructionState.Returned
         6 -> InstructionState.Revival
         else -> throw Exception("new state Added: $_requestStateId")
     }
}


class Fuck(
    /*@SerializedName("requestDescriptions")
    val description: String?,*/
    /*@SerializedName("userId")
    val userId: String, //user id ersal konnade*/
//@SerializedName("requestDate") val requestCreationDate: String,
/*@SerializedName("requestingUserId") val requestingUserId: String,
@SerializedName("hasAccessToDo") val hasAccessToDo: Boolean,
@SerializedName("userFullName") val userFullName: String,
@SerializedName("requestingUserFullName") val requestingUserFullName: String,
@SerializedName("equipmentId") val equipmentId: Int,
@SerializedName("repairGroupId") val repairGroupId: Int,
@SerializedName("logDescription") val logDescription: String?,
@SerializedName("canConfirmation") val canConfirmation: Boolean,
@SerializedName("canCancelConfirmation") val canCancelConfirmation: Boolean,
@SerializedName("determinantNextPeriod") val determinantNextPeriod: Boolean,
@SerializedName("rankingPermission") val rankingPermission: Boolean,
@SerializedName("ranking") val ranking: String?*/
)