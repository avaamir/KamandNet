package mp.amir.ir.kamandnet.respository.persistance.instructiondb

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun update(item: Instruction, onActionDone: (() -> Unit)? = null) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            dao.update(item)
            withContext(Main) {
                onActionDone?.invoke()
            }
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


    /**
     * amaliyat sync kardan server va local ba in method etefagh mioftad
     * dar in method list daryaft shode az server be onvan vorudi tabe
     * gerefte mishavad va list ghadimi niz az localDb gerefte mishavad
     * ba moghayese 2 list az tarigh method  'diffSourceFromNewValues' ke dar
     * 2 list moghayese mishavand va 3 halat update, delete va insert pish miayad
     * va bar asas an localDB ra ba server sync mikonim
     *
     * code ha be soorat modular hast va az tabe 'diffSourceFromNewValues' mitavan baraye moghayese har
     * 2 listi estefade kard va 2 vorudi be soorat interface migirad ke kar ba an serahatan moshakhas ast
     *
     * dar file MyListDiffUtil.kt method tarif shode ast va mitavanid an ra barrasi konid, say shode ast ke
     * az nazar performance khoob amal konad dar soorati ke algorithem behtari barye moghayese 2 list darid
     * mitavinid 'MyListDiffUtil.kt' ra taghir dahid va chun code ha modular neveshte shode ast nizi be hich taghiri
     * dar in file nist
     * */
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
                        /**pak kardan ax haye marbut be instruction haee ke bayad delete shavand*/
                        deleteRelatedImageFiles(nonDoneItems) //TODO not tested
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

    private fun deleteRelatedImageFiles(deletingInstructions: List<Instruction>) {
        CoroutineScope(IO).launch {
            deletingInstructions.forEach {
                it.submitFlowModel?.images?.forEach { image ->
                    image.delete()
                }
            }
        }
    }
}
