package mp.amir.ir.kamandnet.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mp.amir.ir.kamandnet.respository.RemoteRepo
import mp.amir.ir.kamandnet.respository.persistance.instructiondb.InstructionsRepo
import mp.amir.ir.kamandnet.utils.general.DoubleTrigger

class MainActivityViewModel : ViewModel() {



    private val filterKey = MutableLiveData<String>()
    private val getInstructionsFromServerEvent = MutableLiveData<Unit>()

    val getInstructionsFromServerResponse = Transformations.switchMap(getInstructionsFromServerEvent) {
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
}