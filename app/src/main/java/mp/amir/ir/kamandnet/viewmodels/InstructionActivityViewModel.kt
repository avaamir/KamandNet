package mp.amir.ir.kamandnet.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.api.SubmitFlowModel
import mp.amir.ir.kamandnet.models.enums.SendingState
import mp.amir.ir.kamandnet.models.enums.TagType
import mp.amir.ir.kamandnet.respository.persistance.instructiondb.InstructionsRepo
import mp.amir.ir.kamandnet.utils.general.now
import java.io.File
import java.io.FileOutputStream

class InstructionActivityViewModel : ViewModel() {

    var instruction: Instruction? = null
        set(value) {
            field = value
            instructionToSave = value!!.copy()
        }

    private lateinit var instructionToSave: Instruction

    private val submitFlowEvent = MutableLiveData<SubmitFlowModel>()
    /*val submitInstructionResponse = Transformations.switchMap(submitFlowEvent) {
        RemoteRepo.submitInstruction(it.id, it.description, it.tagCode, it.date, it.images)
    }*/

    //TODO jaye bahs darad, aya ba dokme sync kar konad ya ba save ham dar server upload shavad???
    /*fun submitToServer() {
        if (instructionToSave.canUpload) {
            if (instructionToSave.submitFlowModel?.description != null)
                submitFlowEvent.value = instructionToSave.submitFlowModel
        } else
            throw Exception("scanned tag code and instruction tagCode are not equal")
    }*/

    fun submitResult(
        description: String? = null,
        scannedTagCode: String? = null
    ) {
        if (description == null && scannedTagCode == null)
            throw Exception("at least one argument should not be null")
        if (instructionToSave.submitFlowModel == null) {
            instructionToSave.submitFlowModel = SubmitFlowModel(
                description,
                scannedTagCode,
                now().toString(),
                arrayListOf()
            )
        } else {
            if (description != null) {
                instructionToSave.submitFlowModel!!.description = description
            }
            if (scannedTagCode != null) {
                instructionToSave.submitFlowModel!!.scannedTagCode = scannedTagCode
                instructionToSave.submitFlowModel!!.doneDate = now().toString()
            }
        }
        InstructionsRepo.update(instructionToSave.apply {
            //TODO condition check shavad , makhsusan bakhsh scannedTagCode
            val condition = (tagType == TagType.None || (submitFlowModel?.scannedTagCode == tagCode)) && !submitFlowModel?.description.isNullOrEmpty()
            if(condition)
                sendingState = SendingState.Ready
        })
    }

    fun foo() {
        instructionToSave.sendingState = SendingState.Ready
        instructionToSave.submitFlowModel = SubmitFlowModel().apply {
            scannedTagCode = "123"
            description = "salam"
        }
        InstructionsRepo.update(instructionToSave)
    }


    fun saveImage(context: Context, bitmap: Bitmap, turn: Int) {
        if (turn > 6 || turn < 1)
            throw Exception("framePic$turn does not exist, faghat 5 ta ax mishe upload kard")
        val images = instructionToSave.submitFlowModel?.images
        if (images != null && images.size >= turn) {
            val image = images.removeAt(turn - 1)
            if (image.exists())
                image.delete()
        }

        Thread {
            val destFile = File("${context.cacheDir.path}${File.separator}${now()}.jpg")

            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(destFile.absolutePath)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            } finally {
                fileOutputStream?.run {
                    flush()
                    close()
                    if (instructionToSave.submitFlowModel == null) {
                        instructionToSave.submitFlowModel = SubmitFlowModel()
                    }
                    instructionToSave.submitFlowModel!!.images.add(destFile)
                    InstructionsRepo.update(instructionToSave)
                }
            }
        }.run()

    }


}


