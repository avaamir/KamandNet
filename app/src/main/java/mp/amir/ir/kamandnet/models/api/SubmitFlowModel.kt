package mp.amir.ir.kamandnet.models.api

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import mp.amir.ir.kamandnet.respository.persistance.typeconverter.FileListTypeConverter
import java.io.File


/**
*
 * in kelas dar local karbord darad, be in soorat ke har instruction ke az server daryaft mishavad yek SubmitFlowModel ham dar an
 * be soorat local gharar migirad, matn haee ke karbar dar local zakhire mikonad dar description va ax haee ke migirad dar images zakhire mishavad
 * doneDate tarikhi hast ke karbar tag ra scan karde hast, har zaman ke tag scan mishavad doneDate niz zakhire mishavad
 *
 * dar soorati ke khode instruction yek logDescription dashte bashad dar hengam daryaft dar SumbmitFlowModel.description niz zakhire mishavad,
 * elat estefade nakardan az logDescription darun khode Instruction in hast ke code ha pakhsh mishod va dar in halat hame item haye marbut be zakhire sazi
 * dar local dar hamin kelas hast va code tamiz tar ast
*
* */

@Parcelize
data class SubmitFlowModel(
    @ColumnInfo(name = "user_flow_desc")
    var description: String? = null,
    var scannedTagCode: String? = null,
    var doneDate: String? = null,
    @TypeConverters(FileListTypeConverter::class)
    var images: ArrayList<File> = arrayListOf()
) : Parcelable