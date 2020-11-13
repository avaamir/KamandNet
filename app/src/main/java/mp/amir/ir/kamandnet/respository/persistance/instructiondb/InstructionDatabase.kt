package mp.amir.ir.kamandnet.respository.persistance.instructiondb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mp.amir.ir.kamandnet.models.Instruction


@Database(entities = [Instruction::class], version = 6, exportSchema = false)
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