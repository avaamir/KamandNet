package mp.amir.ir.kamandnet.models.enums

enum class InstructionState(val id: Int) {
    Started(1),
    Done(2),
    Confirmed(3),
    Returned(4),
    Cancelled(5),
    Revival(6)
}
