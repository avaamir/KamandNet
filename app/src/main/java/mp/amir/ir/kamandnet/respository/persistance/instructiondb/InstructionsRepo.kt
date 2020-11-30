package mp.amir.ir.kamandnet.respository.persistance.instructiondb

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.api.SubmitFlowModel
import mp.amir.ir.kamandnet.models.enums.InstructionState
import mp.amir.ir.kamandnet.models.enums.SendingState
import mp.amir.ir.kamandnet.utils.general.EqualityCallback
import mp.amir.ir.kamandnet.utils.general.OnSourceListChange
import mp.amir.ir.kamandnet.utils.general.diffSourceFromNewValues


object InstructionsRepo {
    private lateinit var job: Job

    private lateinit var dao: InstructionDao


    fun init(context: Context) {
        dao = InstructionDatabase.getInstance(context).getDao()
    }

    val allInstructions: LiveData<List<Instruction>> by lazy {
        dao.allInstruction
    }

    fun getInstructionById(id: Int): LiveData<Instruction> = dao.getItemById(id)


    fun insert(items: List<Instruction>) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            dao.insertAll(items)
        }
    }

    fun insert(item: Instruction) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            dao.insert(item)
        }
    }

    fun update(item: Instruction) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            dao.update(item)
        }
    }

    fun delete(item: Instruction) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            dao.delete(item)
        }
    }

    fun deleteAllInstructions() {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            dao.deleteAllInstruction()
        }
    }

    fun cancelJobs() {
        if (::job.isInitialized && job.isActive)
            job.cancel()
    }

    fun search(keyword: String): LiveData<List<Instruction>> {
        if (!InstructionsRepo::job.isInitialized || !job.isActive)
            job = Job()
        return dao.search("%$keyword%")
    }

    /*fun uploaded(requestId: Int, isUploaded: Boolean) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            dao.uploaded(requestId, isUploaded)
        }
    }*/

    fun updateAll(items: List<Instruction>) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            dao.updateAll(items)
        }
    }


    fun insertOrUpdate(items: List<Instruction>) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()

        Thread {
            val updateList = ArrayList<Instruction>()
            dao.getAllInstructions().also {
                println("Debux:oldList: $it")
                println("Debux:newList: $items")
            }
                .diffSourceFromNewValues(items, object : EqualityCallback<Instruction> {
                    override fun areItemsSame(oldItem: Instruction, newItem: Instruction) =
                        oldItem.id == newItem.id

                    override fun areContentsSame(
                        oldItem: Instruction,
                        newItem: Instruction
                    ): Boolean {
                        /*//age ghablan sent shode dg nabayad miumade vali umade, pas dobare bayad bere to vaziyat NotReady
                        //chun alan filter ruye instruction.state hast dg niazi be in shart nist, be mahz submit shodan ins.state update mishe va age inja biad yaani started bude va automatic update mishe
                        if (oldItem.sendingState == SendingState.Sent) {
                            return true
                        }*/

                        return oldItem.repairGroupTitle == newItem.repairGroupTitle &&
                                oldItem._repairTypeId == newItem._repairTypeId &&
                                oldItem._tagTypeId == newItem._tagTypeId &&
                                oldItem.tagCode == newItem.tagCode &&
                                oldItem._requestStateId == newItem._requestStateId &&
                                oldItem.jobType == newItem.jobType &&
                                oldItem.date == newItem.date &&
                                oldItem.nodeInstance == newItem.nodeInstance &&
                                oldItem.nodeType == newItem.nodeType &&
                                oldItem.description == newItem.description &&
                                //age logDescription dashtim bayad save beshe dar submitFlowResult.description
                                (newItem.description.isNullOrBlank() || newItem.description == oldItem.submitFlowModel?.description)
                    }

                }, object : OnSourceListChange<Instruction> {
                    override fun onAddItems(items: List<Instruction>) {
                        println("Debux:Inserted: $items")
                        dao.insertAllNonSuspend(items)
                    }

                    override fun onUpdateItem(oldItem: Instruction, newItem: Instruction) {
                        val itemToSave = oldItem.copy(
                            _repairTypeId = newItem._repairTypeId,
                            repairGroupTitle = newItem.repairGroupTitle,
                            _tagTypeId = newItem._tagTypeId,
                            tagCode = newItem.tagCode,
                            _requestStateId = newItem._requestStateId,
                            stateTitle = newItem.stateTitle,
                            jobType = newItem.jobType,
                            date = newItem.date,
                            nodeInstance = newItem.nodeInstance,
                            nodeType = newItem.nodeType
                        )
                        //age logDescription dashtim bayad save beshe dar submitFlowResult.description //conflict join beshe baham
                        if (!newItem.description.isNullOrBlank() && newItem.description != oldItem.submitFlowModel?.description) {
                            (itemToSave.submitFlowModel ?: SubmitFlowModel().also {
                                itemToSave.submitFlowModel = it
                            }).apply {
                                description = if (description.isNullOrBlank())
                                    newItem.description
                                else
                                    "$description - ${newItem.description}"
                            }
                        }
                        //age ghablan sent shode dg nabayad miumade vali umade, pas dobare bayad bere to vaziyat NotReady
                        if (itemToSave.sendingState == SendingState.Sent)
                            itemToSave.sendingState = SendingState.NotReady
                        updateList.add(itemToSave)
                    }

                    override fun onRemoveItems(items: List<Instruction>) {
                        println("Debux:Removed: $items")
                        val nonDoneItems = items.filter {
                            it.state != InstructionState.Done
                        }
                        dao.deleteNonSuspend(nonDoneItems) //unaee ke done shodan ro vase history taraf mikham, age done shode bud pakesh nemekinma az repo
                    }

                    override fun onFinished(newList: ArrayList<Instruction>) {
                        if (updateList.isNotEmpty()) {
                            println("Debux:Updated: $updateList")
                            dao.updateNonSuspend(updateList)
                        }
                    }
                })
        }.run()
    }
}
