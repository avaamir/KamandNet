package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_q_r_scanner.*
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.utils.general.toast
import mp.amir.ir.kamandnet.utils.kamand.Constants

class QRScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var instruction: Instruction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_scanner)

        if (!::instruction.isInitialized)
            instruction = intent.getParcelableExtra(Constants.INTENT_INSTRUCTION_DATA)!!

        codeScanner()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun codeScanner() {
        codeScanner = CodeScanner(this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback { result ->
                runOnUiThread {
                    if (instruction.tagCode == result.text) {
                        toast(result.text)
                        setResult(
                            RESULT_OK,
                            Intent().putExtra(Constants.INTENT_SCAN_TAG_RESULT_TEXT, result.text)
                        )
                        finish()
                    } else {
                        toast("این تگ مربوطه به این دستور کار نمیباشد")
                    }
                }
            }

            errorCallback = ErrorCallback { exception ->
                exception.message?.let { toast(it) }
            }

        }
    }
}