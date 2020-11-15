package mp.amir.ir.kamandnet.respository.persistance.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object FileListTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromModel(images: List<File>): String {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(images.map { it.absolutePath }, type).also { println("debug: $it") }
    }

    @JvmStatic
    @TypeConverter
    fun toModel(jsonImages: String): ArrayList<File> {
        println("debug: $jsonImages")
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson<List<String>>(jsonImages, type).mapTo(arrayListOf()) { File(it) }
    }

}