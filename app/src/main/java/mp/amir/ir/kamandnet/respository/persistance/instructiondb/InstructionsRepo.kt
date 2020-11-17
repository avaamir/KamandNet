package mp.amir.ir.kamandnet.respository.persistance.instructiondb

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mp.amir.ir.kamandnet.models.Instruction


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
        CoroutineScope(IO + job).launch {
            val sourceList = dao.getAllInstructions()
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = sourceList.size

                override fun getNewListSize(): Int = items.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                   return items[newItemPosition].id == sourceList[oldItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    //TODO just check createdDateTime
                    return items[newItemPosition] == sourceList[oldItemPosition]
                }
            })

            result.

        }

    }
}