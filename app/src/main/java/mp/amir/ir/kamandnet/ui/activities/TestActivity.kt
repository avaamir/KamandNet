package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_test.*
import mp.amir.ir.kamandnet.BuildConfig
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.utils.general.now
import mp.amir.ir.kamandnet.utils.general.toast
import okio.IOException
import java.io.File
import kotlin.math.max
import kotlin.math.min


class TestActivity : AppCompatActivity() {
    companion object {
        const val CAPTURE_IMAGE_REQ = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        initViews()
    }

    private lateinit var currentPhotoPath: String

    private fun initViews() {
        btnReqApi.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQ) {
            if (resultCode == RESULT_OK) {
                setImageViewFromFile(currentPhotoPath, ivImage)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${now()}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile().also {
                    currentPhotoPath = it.absolutePath
                }
            } catch (ex: IOException) {
                // Error occurred while creating the File
                toast(ex.message.toString())
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQ)
            }

        }
    }

    private fun setImageViewFromFile(path: String, imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

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

}