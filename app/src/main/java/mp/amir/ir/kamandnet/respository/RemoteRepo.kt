package mp.amir.ir.kamandnet.respository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.models.api.Entity
import mp.amir.ir.kamandnet.respository.sharepref.PrefManager
import mp.amir.ir.kamandnet.utils.fakeInstructions
import mp.amir.ir.kamandnet.utils.fakeUser
import mp.amir.ir.kamandnet.utils.general.RunOnceLiveData
import mp.amir.ir.kamandnet.utils.general.launchApi
import retrofit2.Response
import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction1


object RemoteRepo {
    private lateinit var serverJobs: CompletableJob

    private fun <ResM, ReqM> apiReq(
        request: ReqM,
        requestFunction: KSuspendFunction1<ReqM, Response<Entity<ResM>>>,
        repoLevelHandler: ((Response<Entity<ResM>>) -> (Unit))? = null
    ): RunOnceLiveData<Entity<ResM>?> {
        if (!RemoteRepo::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<Entity<ResM>?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = requestFunction(request)
                    repoLevelHandler?.invoke(response)
                    CoroutineScope(Main).launch {
                        value = response.body()
                    }
                }) {
                    CoroutineScope(Main).launch {
                        value = null
                    }
                }
            }
        }
    }

    private fun <ResM> apiReq(
        requestFunction: KSuspendFunction0<Response<Entity<ResM>>>,
        repoLevelHandler: ((Response<Entity<ResM>>) -> (Unit))? = null  //This will only excute if LiveData has an observer, because it called on active method
    ): RunOnceLiveData<Entity<ResM>?> {
        if (!RemoteRepo::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<Entity<ResM>?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = requestFunction()
                    repoLevelHandler?.invoke(response)
                    CoroutineScope(Main).launch {
                        value = response.body()
                    }
                }) {
                    CoroutineScope(Main).launch {
                        value = null
                    }
                }
            }
        }
    }


    fun login(username: String, password: String, onResponse: (Entity<User?>?) -> Unit) {
        if (!RemoteRepo::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        val user = fakeUser(username)
        PrefManager.saveUser(user)
        onResponse(Entity(user, "", true))
        //TODO connect it to server
    }

    fun logout(onResponse: (String?) -> Unit) {
        TODO("Not yet implemented")
    }

    fun getInstructions() : LiveData<Entity<List<Instruction>>?> {
        return MutableLiveData(Entity(fakeInstructions() , "", true))
    }
}