package mp.amir.ir.kamandnet.respository.persistance.instructiondb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.respository.persistance.typeconverter.FileListTypeConverter
import mp.amir.ir.kamandnet.respository.persistance.typeconverter.SendingStateConverter

@TypeConverters(FileListTypeConverter::class, SendingStateConverter::class)
@Database(entities = [Instruction::class], version = 16, exportSchema = false)
abstract class InstructionDatabase : RoomDatabase() {

    abstract fun getDao(): InstructionDao

    companion object {
        @Volatile
        private var INSTANCE: InstructionDatabase? = null


        fun getInstance(context: Context): InstructionDatabase =
            INSTANCE
                ?: synchronized(this) { //double check lock algorithm
                    INSTANCE
                        ?: buildDatabase(
                            context
                        ).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                InstructionDatabase::class.java, "instructions.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}