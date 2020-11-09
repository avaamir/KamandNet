package mp.amir.ir.kamandnet.viewmodels

import androidx.lifecycle.*
import mp.amir.ir.kamandnet.models.UpdateResponse
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.models.api.Entity
import mp.amir.ir.kamandnet.respository.RemoteRepo
import mp.amir.ir.kamandnet.respository.UserConfigs
import mp.amir.ir.kamandnet.respository.persistance.instructiondb.InstructionsRepo
import mp.amir.ir.kamandnet.utils.general.DoubleTrigger
import mp.amir.ir.kamandnet.utils.general.Event

class MainActivityViewModel : ViewModel() {

    val user: User get() = UserConfigs.user.value!!

    //TODO read from db
    val messageCount: LiveData<String> = MutableLiveData()


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
            //TODO faghat un dastur karaee ro neshun bede ke done nashodan, age response az server umad
            //TODO va done shode budan nabayad neshun dade beshan, in kar bayad dar repo level etefagh biofte
            val keyword = it.first
            if (keyword.isNullOrBlank()) {
                InstructionsRepo.search(keyword = "%")
            } else {
                InstructionsRepo.search(keyword = keyword)
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
        TODO("Not yet implemented")
    }
}