package mp.amir.ir.kamandnet.respository.persistance.instructiondb

import androidx.lifecycle.LiveData
import androidx.room.*
import mp.amir.ir.kamandnet.models.Instruction

@Dao
interface InstructionDao {

    @Query("SELECT * FROM instructions WHERE id=:id")
    fun getItemById(id: Int): LiveData<Instruction>

    @get:Query("SELECT * FROM instructions")
    val allInstruction: LiveData<List<Instruction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Instruction)

    @Update
    suspend fun update(item: Instruction)

    @Delete
    suspend fun delete(item: Instruction)

    @Query("DELETE FROM instructions")
    suspend fun deleteAllInstruction()

    @Query("SELECT * FROM instructions WHERE id = :id")
    suspend fun exists(id: Int): Instruction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Instruction>)

    @Query("SELECT * FROM instructions WHERE nodeInstance Like :keyword OR nodeType Like :keyword OR jobType Like :keyword OR repairTypeTitle Like :keyword")
    fun search(keyword: String): LiveData<List<Instruction>>

   /* @Query("UPDATE instructions SET isUploaded = :uploaded WHERE id = :id")
    suspend fun uploaded(id: Int, uploaded: Boolean)*/

    @Update
    suspend fun updateAll(items: List<Instruction>)

    suspend fun getAllInstructions(): List<Instruction>
}