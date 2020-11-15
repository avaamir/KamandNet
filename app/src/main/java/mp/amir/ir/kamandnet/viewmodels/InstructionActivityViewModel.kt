package mp.amir.ir.kamandnet.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.api.SubmitFlowModel
import mp.amir.ir.kamandnet.respository.RemoteRepo
import mp.amir.ir.kamandnet.utils.general.now
import java.io.File

class InstructionActivityViewModel : ViewModel() {
    fun submitInstruction(description: String) {
        TODO("Not yet implemented")
    }

    val images = arrayListOf<File>()

    var scannedTagCode: String? = null
    var instruction: Instruction? = null

    private val submitFlowEvent = MutableLiveData<SubmitFlowModel>()
    /*val submitInstructionResponse = Transformations.switchMap(submitFlowEvent) {
        RemoteRepo.submitInstruction(it.id, it.description, it.tagCode, it.date, it.images)
    }*/

    /*fun submitInstruction(
        description: String
    ) {
        if (scannedTagCode == instruction!!.tagCode)
            submitFlowEvent.value =
                SubmitFlowModel(
                    instruction!!.id,
                    description,
                    scannedTagCode!!,
                    now().toString(),
                    images
                )
        else
            throw Exception("scanned tag code and instruction tagCode are not equal")
    }*/
}


