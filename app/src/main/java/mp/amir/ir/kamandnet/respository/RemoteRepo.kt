package mp.amir.ir.kamandnet.respository

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.api.Entity
import mp.amir.ir.kamandnet.models.api.LoginRequest
import mp.amir.ir.kamandnet.models.enums.InstructionState
import mp.amir.ir.kamandnet.models.enums.SendingState
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.respository.persistance.instructiondb.InstructionsRepo
import mp.amir.ir.kamandnet.utils.kamand.fakeInstructions
import mp.amir.ir.kamandnet.utils.general.RunOnceLiveData
import mp.amir.ir.kamandnet.utils.general.launchApi
import mp.amir.ir.kamandnet.utils.general.now
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction1


/**
* tamami api ha az tarigh in kelass call mishavad
 * amaliyat haee ke dar laye haye repo mesl zakhire dar local va .. anjam shavad va amade sazi response barye
 * enteghal az thread IO be UI dar in file anjam shode ast
 *
 * deghat shavad endpoint ha dar kelass KamandClient.kt hast
* */

object RemoteRepo {
    private lateinit var serverJobs: CompletableJob

    private fun <ResM, ReqM> apiReq(
        requestFunction: KSuspendFunction1<ReqM, Response<Entity<ResM>>>,
        request: ReqM,
        repoLevelHandler: ((Response<Entity<ResM>>) -> (Unit))? = null
    ): RunOnceLiveData<Entity<ResM>?> {
        return object : RunOnceLiveData<Entity<ResM>?>() {
            override fun onActiveRunOnce() {
                if (!RemoteRepo::serverJobs.isInitialized || !serverJobs.isActive) serverJobs =
                    Job()
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = requestFunction(request)
                    repoLevelHandler?.invoke(response)
                    withContext(Main) {
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
        return object : RunOnceLiveData<Entity<ResM>?>() {
            override fun onActiveRunOnce() {
                if (!RemoteRepo::serverJobs.isInitialized || !serverJobs.isActive) serverJobs =
                    Job()
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = requestFunction()
                    repoLevelHandler?.invoke(response)
                    withContext(Main) {
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
                UserConfigs.loginUser(user, true)
            }
        }

    fun logout(onResponse: (String?) -> Unit) {
        //todo if succeed
        UserConfigs.logout()
    }

    fun getInstructions() = apiReq(ApiService.client::getInstructions) {
        val response = it.body()
        if (response?.isSucceed == true) {
            InstructionsRepo.insertOrUpdate(response.entity!!)
        }
    }

    //Fake for test purpose
    fun getInstructions2() : RunOnceLiveData<Entity<List<Instruction>>?> {
        return object : RunOnceLiveData<Entity<List<Instruction>>?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO).launch {
                    delay(2000)
                    val data = fakeInstructions() //listOf<Instruction>()
                    InstructionsRepo.insertOrUpdate(data)
                    withContext(Main) {
                        value = Entity(data, true, "")
                    }
                }
            }

        }
    }

    fun checkUpdates() = apiReq(ApiService.client::checkUpdates)

    fun submitInstruction(
        instruction: Instruction,
    ): RunOnceLiveData<Entity<Any>?> {
        val flow = instruction.submitFlowModel!!
        val requestId = instruction.id
        val description = flow.description!!
        val tagCode = flow.scannedTagCode
        val date = flow.doneDate ?: now().toString() //TODO  age tagCode nadasht che tarikhi bokhore???
        val images = flow.images
        //

        val idPart = requestId.toString().toRequestBody("text/plane".toMediaTypeOrNull())
        val descPart = description.toRequestBody("text/plane".toMediaTypeOrNull())
        val tagPart = tagCode?.toRequestBody("text/plane".toMediaTypeOrNull())
        val datePart = date.toRequestBody("text/plane".toMediaTypeOrNull())
        val imagesPart = arrayListOf<MultipartBody.Part>().apply {
            images.forEachIndexed { index, file ->
                val requestFile: RequestBody = file
                    .asRequestBody("*/*".toMediaTypeOrNull()) //"image/jpg"

                val part = MultipartBody.Part.createFormData("files$index", file.name, requestFile)
                add(part)
            }
        }

        return object : RunOnceLiveData<Entity<Any>?>() {
            override fun onActiveRunOnce() {
                if (!RemoteRepo::serverJobs.isInitialized || !serverJobs.isActive) serverJobs =
                    Job()
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.client.submitInstructions(
                        idPart,
                        descPart,
                        tagPart,
                        datePart,
                        imagesPart
                    )

                    if (response.isSuccessful) {
                        if (response.body()?.isSucceed == true) {
                            InstructionsRepo.update(
                                instruction.copy(
                                    sendingState = SendingState.Sent,
                                    _requestStateId = InstructionState.Done.id
                                )
                            )
                        }
                    }
                    withContext(Main) {
                        value = response.body()
                    }
                }, {
                    postValue(null)
                })
            }
        }
    }
}