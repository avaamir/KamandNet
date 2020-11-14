package mp.amir.ir.kamandnet.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.api.SubmitFlowRequest
import mp.amir.ir.kamandnet.respository.RemoteRepo
import mp.amir.ir.kamandnet.utils.general.now
import java.io.File

class InstructionActivityViewModel : ViewModel() {
    val images = arrayListOf<File>()

    var scannedTagCode: String? = null
    var instruction: Instruction? = null

    private val submitFlowEvent = MutableLiveData<SubmitFlowRequest>()
    val submitInstructionResponse = Transformations.switchMap(submitFlowEvent) {
        RemoteRepo.submitInstruction(it.id, it.description, it.tagCode, it.date, it.images)
    }

    fun submitInstruction(
        description: String
    ) {
        if (scannedTagCode == instruction!!.tagCode)
            submitFlowEvent.value =
                SubmitFlowRequest(
                    instruction!!.id,
                    description,
                    scannedTagCode!!,
                    now().toString(),
                    images
                )
        else
            throw Exception("scanned tag code and instruction tagCode are not equal")
    }
}


