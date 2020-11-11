package mp.amir.ir.kamandnet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.models.api.Entity
import mp.amir.ir.kamandnet.models.api.LoginRequest
import mp.amir.ir.kamandnet.respository.RemoteRepo

class LoginActivityViewModel : ViewModel() {




    private val loginEvent = MutableLiveData<LoginRequest>()
    val loginResponse: LiveData<Entity<User>?> = Transformations.switchMap(loginEvent) {
        RemoteRepo.login(it)
    }


    fun login(username: String, password: String) {
        loginEvent.value = LoginRequest(username, password)
    }

    fun loginAgain() {
        loginEvent.value = loginEvent.value
    }

}