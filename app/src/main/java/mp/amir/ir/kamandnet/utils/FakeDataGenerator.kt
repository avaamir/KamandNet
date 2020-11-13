package mp.amir.ir.kamandnet.utils

import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.User

/*fun fakeInstructions() = listOf(
    Instruction(1,  "برق","سرویس","12:20 99/08/03","شروع شده","نوار نقاله", "نوار نقاله یک"),
    Instruction(2,  "برق","سرویس","12:20 99/08/03","شروع شده","نوار نقاله", "نوار نقاله یک"),
)*/


 fun fakeUser(username: String): User = User("!", username, null)