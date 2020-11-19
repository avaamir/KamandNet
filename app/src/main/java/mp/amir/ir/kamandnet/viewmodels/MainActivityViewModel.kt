package mp.amir.ir.kamandnet.viewmodels

import androidx.lifecycle.*
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.UpdateResponse
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.models.api.Entity
import mp.amir.ir.kamandnet.models.enums.InstructionState
import mp.amir.ir.kamandnet.models.enums.RepairType
import mp.amir.ir.kamandnet.models.enums.SendingState
import mp.amir.ir.kamandnet.respository.RemoteRepo
import mp.amir.ir.kamandnet.respository.UserConfigs
import mp.amir.ir.kamandnet.respository.persistance.instructiondb.InstructionsRepo
import mp.amir.ir.kamandnet.utils.general.DoubleTrigger
import mp.amir.ir.kamandnet.utils.general.Event
import mp.amir.ir.kamandnet.utils.general.now

class MainActivityViewModel : ViewModel() {

    val user: User get() = UserConfigs.userVal!!

    //TODO read from db
    val messageCount: LiveData<String> = MutableLiveData()


    private val submitFlowEvent = MutableLiveData<Instruction>()
    val submitInstructionResponse = Transformations.switchMap(submitFlowEvent) { _instruction ->
        val flow = _instruction.submitFlowModel!!
        RemoteRepo.submitInstruction(
            _instruction.id,
            flow.description!!,
            flow.scannedTagCode,
            flow.doneDate ?: now().toString(), //TODO  age tagCode nadasht che tarikhi bokhore???
            flow.images
        ).map { response ->
            InstructionsRepo.update(_instruction.apply { //todo behtare in Repo.UPDATE ro be repo level bord va yek suspend fun nevesht vasash ke ye coroutine dg ijad nashe
                if (response?.isSucceed == true) {
                    _instruction.sendingState = SendingState.Sent
                } else {
                    _instruction.sendingState = SendingState.Ready
                }
            })
            response
        }
    }


    private var isCheckedForUpdatesRequestActive = false

    private var updateEvent: Event<Entity<UpdateResponse>> =
        Event(Entity(UpdateResponse.NoResponse, false, null))

    private val updateRequestEvent = MutableLiveData<Event<Unit>>()
    val checkUpdateResponse = Transformations.switchMap(updateRequestEvent) {
        isCheckedForUpdatesRequestActive = true
        RemoteRepo.checkUpdates().map {
            if (it?.isSucceed == false) {
                isCheckedForUpdatesRequestActive = false
            }
            if (it != null) {
                if (!updateEvent.peekContent().isSucceed) {
                    updateEvent = Event(it)
                }
            }
            updateEvent
        }
    }


    private val filterKey = MutableLiveData<String>()
    private val getInstructionsFromServerEvent = MutableLiveData<Unit>()

    val getInstructionsFromServerResponse =
        Transformations.switchMap(getInstructionsFromServerEvent) {
            RemoteRepo.getInstructions()
        }

    val instructions =
        Transformations.switchMap(DoubleTrigger(filterKey, InstructionsRepo.allInstructions)) {
            val keyword = it.first
            if (keyword.isNullOrBlank()) {
                InstructionsRepo.search(keyword = "%")
            } else {
                InstructionsRepo.search(keyword = keyword)
            }.map { _instructions ->
                _instructions.filter { _instr ->
                    //_instr.sendingState != SendingState.Sent ||
                            _instr.state == InstructionState.Started //TODO che state haee ro neshun bedam?
                }.sortedWith(compareByDescending { item ->
                    item.repairType == RepairType.EM
                })
            }
        }

    fun filterList(filterKey: String?) {
        if (this.filterKey.value != filterKey)
            this.filterKey.value = filterKey
    }

    fun getInstructionsFromServer() {
        getInstructionsFromServerEvent.value = Unit
    }

    fun checkUpdates() {
        if (false) { //TODO delete this line after Server Implemented
            updateRequestEvent.value = Event(Unit)
        }
    }

    fun logout() {
        UserConfigs.logout()
    }

    fun submitInstructionResult(instruction: Instruction) {
        submitFlowEvent.value = instruction
    }


    fun sync() {
        /*instructions.value!!.forEach { //todo BtnSync faghat bayad zamani neshun dade beshe ke chizi tu list hast (ke Ready bashe(ekhtiari))
            if (it.sendingState == SendingState.Ready) {
                it.sendingState = SendingState.Sending
                submitFlowEvent.value = it
                InstructionsRepo.update(it)
            }
        }*/
        InstructionsRepo.updateAll(
            instructions.value!!.mapNotNull {
                if (it.sendingState == SendingState.Ready || it.sendingState == SendingState.Sending) {
                    it.sendingState = SendingState.Sending
                    submitFlowEvent.value = it
                    it
                } else {
                    null
                }
            }
        )
    }
}
