package mp.amir.ir.kamandnet.respository.sharepref

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mp.amir.ir.kamandnet.models.User

object PrefManager {
    private const val MY_PREFS_NAME = "prefs"
    private const val USER_TAG = "user"
    private const val BASE_URL_TAG = "b-url"

    private lateinit var prefs: SharedPreferences


    val isUserLoggedIn get() = prefs.getString(USER_TAG, null) != null


    val baseURL: String? get() = prefs.getString(BASE_URL_TAG, null)

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

    fun saveUser(user: User, blocking: Boolean) {
        val gson = Gson()
        val type = object : TypeToken<User>() {}.type
        val userJson = gson.toJson(user, type)

        if (blocking)
            prefs.edit().putString(USER_TAG, userJson).commit()
        else
            prefs.edit().putString(USER_TAG, userJson).apply()
    }

    fun getLoggedInUser(): User? {
        val userJson = prefs.getString(USER_TAG, null) ?: return null

        val gson = Gson()
        val type = object : TypeToken<User>() {}.type
        return gson.fromJson(userJson, type)
    }

    fun saveDomain(domain: String) {
        prefs.edit().putString(BASE_URL_TAG, domain).apply()
    }

}