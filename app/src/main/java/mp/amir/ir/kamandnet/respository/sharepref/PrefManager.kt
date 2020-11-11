package mp.amir.ir.kamandnet.respository.sharepref

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mp.amir.ir.kamandnet.models.User

object PrefManager {
    private const val MY_PREFS_NAME = "prefs"
    private const val USER_TAG = "user"

    private lateinit var prefs: SharedPreferences


    val isUserLoggedIn get() = prefs.getString(USER_TAG, null) != null


    fun init(context: Context) {
        if (!this::prefs.isInitialized) {
            prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun flush() {
        prefs.edit().clear().apply()
    }

    fun clearUser() {
        prefs.edit().remove(USER_TAG).apply()
    }

    fun saveUser(user: User) {
        val gson = Gson()
        val type = object : TypeToken<User>() {}.type
        val userJson = gson.toJson(user, type)

        prefs.edit().putString(USER_TAG, userJson).commit()
    }

    fun getLoggedInUser(): User? {
        val userJson = prefs.getString(USER_TAG, null) ?: return null

        val gson = Gson()
        val type = object : TypeToken<User>() {}.type
        return gson.fromJson(userJson, type)
    }

}