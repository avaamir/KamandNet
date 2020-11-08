package mp.amir.ir.kamandnet.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "instructions")
@Parcelize
data class Instruction(
    @PrimaryKey
    val id: String,
    val name: String,
    val repairType: String,
    val jobType: String,
    val date: String,
    val status: String,
    val nodeType: String,
    val nodeInstance: String
) : Parcelable