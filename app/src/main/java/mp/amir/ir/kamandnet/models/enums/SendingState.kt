package mp.amir.ir.kamandnet.models.enums

enum class SendingState(val id: Int) {
    NotReady(0),
    Ready(1),
    Sending(2),
    Sent(3)
}