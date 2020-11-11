package mp.amir.ir.kamandnet.respository

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.api.Entity
import mp.amir.ir.kamandnet.models.api.LoginRequest
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.respository.persistance.instructiondb.InstructionsRepo
import mp.amir.ir.kamandnet.utils.fakeInstructions
import mp.amir.ir.kamandnet.utils.general.RunOnceLiveData
import mp.amir.ir.kamandnet.utils.general.launchApi
import retrofit2.Response
import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction1


object RemoteRepo {
    private lateinit var serverJobs: CompletableJob

    private fun <ResM, ReqM> apiReq(
        requestFunction: KSuspendFunction1<ReqM, Response<Entity<ResM>>>,
        request: ReqM,
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


    fun login(loginRequest: LoginRequest) =
        apiReq(ApiService.client::login, loginRequest) {
            val userEntity = it.body()
            if (userEntity?.isSucceed == true) {
                val user = userEntity.entity!!
                UserConfigs.loginUser(user)
            }
        }

    /*fun login(username: String, password: String, onResponse: (Entity<User>?) -> Unit) {
        if (!RemoteRepo::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        CoroutineScope(IO + serverJobs).launchApi({
            val response = ApiService.client.login(LoginRequest(username, password))
            val userEntity = response.body()
            if (userEntity?.isSucceed == true) {
                val user = userEntity.entity!!
                UserConfigs.loginUser(user)
            }
            withContext(Main) {
                onResponse(userEntity)
            }
        }, {
            onResponse(null)
        })
    }*/

    fun logout(onResponse: (String?) -> Unit) {
        TODO("Not yet implemented")
    }

    fun getInstructions(): RunOnceLiveData<Entity<List<Instruction>>?> {
        return object : RunOnceLiveData<Entity<List<Instruction>>?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO).launch {
                    delay(3000)
                    val response = Entity(fakeInstructions(), true, "")
                    if (response.isSucceed) {
                        InstructionsRepo.insert(response.entity!!)
                    }
                    postValue(response)
                }
            }
        }
    }

    fun checkUpdates() = apiReq(ApiService.client::checkUpdates)
}