package mp.amir.ir.kamandnet.respository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.respository.sharepref.PrefManager

object UserConfigs {
    private val userLive = MutableLiveData<User?>(null)
    val user: LiveData<User?> get() = userLive
    val isLoggedIn get() = PrefManager.isUserLoggedIn

    init {
        val user = PrefManager.getLoggedInUser()
        if(user != null) {
            afterLoggedInDo(user)
        }
    }

    fun loginUser(user: User) {
        if (UserConfigs.user.value != user) {
            PrefManager.saveUser(user)
            afterLoggedInDo(user)
        }
    }

    fun logout() {
        PrefManager.clearUser()
        userLive.value = null
    }


    private fun afterLoggedInDo(user: User) {
        ApiService.setToken(user.token)
        userLive.value = user
    }


}