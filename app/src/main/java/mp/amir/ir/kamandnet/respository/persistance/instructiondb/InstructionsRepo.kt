package mp.amir.ir.kamandnet.respository.persistance.instructiondb

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mp.amir.ir.kamandnet.models.Instruction
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
            dao.getNotSentInstructions()
                .diffSourceFromNewValues(items, object : EqualityCallback<Instruction> {
                    override fun areItemsSame(oldItem: Instruction, newItem: Instruction) =
                        oldItem.id == newItem.id

                    override fun areContentsSame(
                        oldItem: Instruction,
                        newItem: Instruction
                    ): Boolean {
                        return oldItem.repairGroupTitle == newItem.repairGroupTitle &&
                                oldItem._repairTypeId == newItem._repairTypeId &&
                                oldItem._tagTypeId == newItem._tagTypeId &&
                                oldItem.tagCode == newItem.tagCode &&
                                oldItem._requestStateId == newItem._requestStateId &&
                                oldItem.jobType == newItem.jobType &&
                                oldItem.date == newItem.date &&
                                oldItem.nodeInstance == newItem.nodeInstance &&
                                oldItem.nodeType == newItem.nodeType
                    }

                }, object : OnSourceListChange<Instruction> {
                    override fun onAddItems(items: List<Instruction>) {
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
                        updateList.add(itemToSave)
                    }

                    override fun onRemoveItems(items: List<Instruction>) {
                        dao.deleteNonSuspend(items)
                    }

                    override fun onFinished(newList: ArrayList<Instruction>) {
                        dao.updateNonSuspend(updateList)
                    }
                })
        }.run()
    }
}
