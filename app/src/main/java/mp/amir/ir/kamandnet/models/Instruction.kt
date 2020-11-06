package mp.amir.ir.kamandnet.models

data class Instruction(
    val id: String,
    val name: String,
    val repairType: String,
    val jobType: String,
    val date: String,
    val status: String
)