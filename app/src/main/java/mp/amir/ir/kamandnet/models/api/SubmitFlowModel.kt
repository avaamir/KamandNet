package mp.amir.ir.kamandnet.models.api

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import mp.amir.ir.kamandnet.respository.persistance.typeconverter.FileListTypeConverter
import java.io.File

@Parcelize
data class SubmitFlowModel(
    @ColumnInfo(name = "user_flow_desc")
    var description: String? = null,
    var scannedTagCode: String? = null,
    var doneDate: String? = null,
    @TypeConverters(FileListTypeConverter::class)
    var images: ArrayList<File> = arrayListOf()
) : Parcelable