package mp.amir.ir.kamandnet.models.enums


/**
* vahti Instruction taze daryaft mishavad state an Started mibashad va vaghti dar server sabt shod
* state an ra dar local be done tabdil mikonim chun be mahz sabt shodan dar server state=done mishavad
*
* in kelas baraye filter kardan item haee ke be karbar namayesh midahim niz karbord darad be in soorat ke
* an haee ke faghat state=Started hast ra neshan midahad
*
* */
enum class InstructionState(val id: Int) {
    Started(1),
    Done(2),
    Confirmed(3),
    Returned(4),
    Cancelled(5),
    Revival(6)
}
