package mp.amir.ir.kamandnet.models.api

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import mp.amir.ir.kamandnet.respository.persistance.typeconverter.FileListTypeConverter
import java.io.File

@Parcelize
data class SubmitFlowModel(
    @ColumnInfo(name = "receiver_uid")
    val userId: String?,
    @ColumnInfo(name = "user_flow_desc")
    val description: String?,
    val scannedTagCode: String?,
    val doneDate: String?,
    @TypeConverters(FileListTypeConverter::class)
    val images: List<File>?
) : Parcelable