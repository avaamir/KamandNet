package mp.amir.ir.kamandnet.utils.general

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.content.FileProvider
import mp.amir.ir.kamandnet.BuildConfig
import mp.amir.ir.kamandnet.ui.activities.TestActivity
import okio.IOException
import java.io.File
import kotlin.math.max
import kotlin.math.min

@Throws(IOException::class)
private fun Activity.createImageFile(): File {
    // Create an image file name
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "JPEG_${now()}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

@Throws(IOException::class)
fun Activity.dispatchTakePictureIntent(captureImageReqId: Int): File? {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    // Ensure that there's a camera activity to handle the intent
    takePictureIntent.resolveActivity(packageManager)?.also {
        // Create the File where the photo should go
        val photoFile = try {
            createImageFile()
        } catch (ex: IOException) {
            // Continue only if the File was successfully created
            throw ex
        }

        val photoURI = FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.provider",
            photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, captureImageReqId)
        return photoFile
    }
    return null
}

fun setImageViewFromFile(path: String, imageView: ImageView) {
    // Get the dimensions of the View
    val targetW: Int = //if (imageView.width == 0) 200 else
        imageView.width
    val targetH: Int = //if(imageView.height == 0) 200 else
        imageView.height

    val bmOptions = BitmapFactory.Options().apply {
        // Get the dimensions of the bitmap
        inJustDecodeBounds = true

        BitmapFactory.decodeFile(path, this)

        val photoW = outWidth
        val photoH = outHeight

        // Determine how much to scale down the image
        val scaleFactor = max(1, min(photoW / targetW, photoH / targetH))

        // Decode the image file into a Bitmap sized to fill the View
        inJustDecodeBounds = false
        inSampleSize = scaleFactor
        inPurgeable = true
    }
    BitmapFactory.decodeFile(path, bmOptions)?.also { bitmap ->
        imageView.setImageBitmap(bitmap)
    }
}