package mp.amir.ir.kamandnet.respository.persistance.userdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mp.amir.ir.kamandnet.models.User


@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null


        fun getInstance(context: Context): UserDatabase =
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
                UserDatabase::class.java, "user.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}