package mp.amir.ir.kamandnet.models.api

import java.io.File

class SubmitFlowRequest(
    val id: Int,
    val description: String,
    val tagCode: String,
    val date: String,
    val images: List<File>
)