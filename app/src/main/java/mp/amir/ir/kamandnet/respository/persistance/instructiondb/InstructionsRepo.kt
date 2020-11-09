package mp.amir.ir.kamandnet.respository.persistance.instructiondb

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mp.amir.ir.kamandnet.models.Instruction


object InstructionsRepo {
    private lateinit var job: Job
    private var context: Context? = null

    private val dao: InstructionDao by lazy {
        InstructionDatabase.getInstance(context!!).getDao().also { context = null }
    }


    fun setContext(context: Context) {
        this.context = context.applicationContext
    }

    val allInstructions: LiveData<List<Instruction>> by lazy {
        dao.allInstruction
    }

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
}