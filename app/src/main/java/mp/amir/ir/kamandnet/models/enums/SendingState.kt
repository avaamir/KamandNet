package mp.amir.ir.kamandnet.models.enums

/**
* in enum dar local karbord darad
* agar instruction taze az server daryaft shavad sendingState an NotReady hast
* agar matn va ax dar an zakhire shod sendingState, Ready mishavad va alamat save zir instruction dide khahad shod
* vaghti dokme sync ya submit click shavad sendingState = sending mishavad va progressbar be jaye alamat save miayad
* agar dar server sabt shod va 200 bargasht sendingState = sent mishavad
*
* */

enum class SendingState(val id: Int) {
    NotReady(0),
    Ready(1),
    Sending(2),
    Sent(3)
}