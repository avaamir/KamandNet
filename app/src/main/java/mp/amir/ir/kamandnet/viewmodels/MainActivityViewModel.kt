package mp.amir.ir.kamandnet.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mp.amir.ir.kamandnet.respository.RemoteRepo

class MainActivityViewModel : ViewModel() {


    private val getInstructionsEvent = MutableLiveData<Unit>()
    val instructions = Transformations.switchMap(getInstructionsEvent) {
        RemoteRepo.getInstructions()
    }

    fun filterList(filterKey: String) {
        TODO("Not yet implemented")
    }

    fun getInstructions() {
       getInstructionsEvent.value = Unit
    }
}