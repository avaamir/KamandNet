package mp.amir.ir.kamandnet.utils

import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.User

fun fakeInstructions() = listOf(
    Instruction("1", "سرویس نوار نقاله", "برق","سرویس","12:20 99/08/03","شروع شده"),
    Instruction("12", "سرویس نوار نقاله1", "برق","سرویس","12:20 99/08/03","شروع شده"),
    Instruction("13", "سرویس نوار نقاله2", "برق","سرویس","12:20 99/08/03","شروع شده"),
    Instruction("14", "سرویس نوار نقاله3", "برق","سرویس","12:20 99/08/03","شروع شده"),
    Instruction("15", "سرویس نوار نقاله4", "برق","سرویس","12:20 99/08/03","شروع شده"),
    Instruction("16", "سرویس نوار نقاله5", "برق","سرویس","12:20 99/08/03","شروع شده"),
    Instruction("17", "6سرویس نوار نقاله", "برق","سرویس","12:20 99/08/03","شروع شده"),
    Instruction("18", "سرویس نوار نقاله7", "برق","سرویس","12:20 99/08/03","شروع شده"),
)


 fun fakeUser(username: String): User = User("!", username, "lhiasdfihoashdf==fasdf", null)