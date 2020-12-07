package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.utils.general.dispatchTakePictureIntent
import mp.amir.ir.kamandnet.utils.general.setImageViewFromFile


class TestActivity : AppCompatActivity() {
    private companion object {
        const val CAPTURE_IMAGE_REQ = 42
    }


    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        initViews()
    }

    private fun initViews() {
        btnReqApi.setOnClickListener {
            dispatchTakePictureIntent(CAPTURE_IMAGE_REQ)?.also {
                currentPhotoPath = it.absolutePath
            }
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
}