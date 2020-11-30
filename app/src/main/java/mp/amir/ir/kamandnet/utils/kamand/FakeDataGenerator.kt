package mp.amir.ir.kamandnet.utils.kamand

import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.models.enums.InstructionState
import mp.amir.ir.kamandnet.models.enums.RepairType
import mp.amir.ir.kamandnet.models.enums.SendingState
import mp.amir.ir.kamandnet.models.enums.TagType

/*fun fakeInstructions() = listOf(
    Instruction(1,  "برق","سرویس","12:20 99/08/03","شروع شده","نوار نقاله", "نوار نقاله یک"),
    Instruction(2,  "برق","سرویس","12:20 99/08/03","شروع شده","نوار نقاله", "نوار نقاله یک"),
)*/


fun fakeUser(username: String): User = User("!", username, "", null)


fun fakeInstructions() = listOf<Instruction>(
        makeInstruction(1, RepairType.CM, TagType.None, InstructionState.Started),
        makeInstruction(2, RepairType.CM, TagType.None, InstructionState.Started),
        makeInstruction(3, RepairType.CM, TagType.None, InstructionState.Started),
        makeInstruction(4, RepairType.CM, TagType.None, InstructionState.Started),
        makeInstruction(5, RepairType.CM, TagType.None, InstructionState.Started),
        makeInstruction(6, RepairType.CM, TagType.None, InstructionState.Started),
)

fun fakeInstructions2() = listOf<Instruction>(
        makeInstruction(3, RepairType.PM, TagType.None, InstructionState.Started),
        makeInstruction(4, RepairType.CM, TagType.None, InstructionState.Started),
        makeInstruction(5, RepairType.CM, TagType.None, InstructionState.Started),
        makeInstruction(6, RepairType.EM, TagType.None, InstructionState.Started),
        makeInstruction(7, RepairType.CM, TagType.None, InstructionState.Started),
)

fun makeInstruction(
        id: Int,
        repairType: RepairType = RepairType.EM,
        tagType: TagType = TagType.None,
        instructionState: InstructionState = InstructionState.Started
) = Instruction(
        id,
        "alaki",
        "alaki",
        "99/2/2",
        "شروع شده",
        "شاخه یک",
        "alaki",
        instructionState.id,
        "",
        tagType.id,
        repairType.id,
        "اورژانسی",
        null,
        SendingState.NotReady
)

