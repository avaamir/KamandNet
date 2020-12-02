package mp.amir.ir.kamandnet.respository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.respository.sharepref.PrefManager

/**
* etelat marbut be session user faal dar app
 * tamami ertebat haee ke ba User bayad anjam shavad mesl login , logout, zakhire token va .. az tarigh in kelass anjam mishavad
* */
object UserConfigs {
    private val userLive = MutableLiveData<User?>(null)
    val user: LiveData<User?> get() = userLive

    private var mUserVal : User? = null
    val userVal get() = mUserVal


    val isLoggedIn get() = PrefManager.isUserLoggedIn

    init {
        val user = PrefManager.getLoggedInUser()
        if(user != null) {
            afterLoggedInDo(user)
        }
    }

    fun loginUser(user: User, blocking: Boolean) {
        if (UserConfigs.user.value != user) {
            PrefManager.saveUser(user, blocking)
            afterLoggedInDo(user)
        }
    }

    fun logout() {
        PrefManager.clearUser()
        userLive.value = null
    }


    private fun afterLoggedInDo(user: User) {
        ApiService.setToken(user.token)
        userLive.postValue(user)
        mUserVal = user
    }


}