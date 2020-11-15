package mp.amir.ir.kamandnet.respository.persistance.typeconverter

import androidx.room.TypeConverter
import java.io.File

object FileListTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromModel(images: List<File>): String {
        return images.joinToString { it.absolutePath }.also { println("debug:$it") }
    }

    @JvmStatic
    @TypeConverter
    fun toModel(joinedImages: String): List<File> = arrayListOf<File>().apply {
        addAll(
            joinedImages.split(",").map { path ->
                File(path)
            }
        )
    }

}