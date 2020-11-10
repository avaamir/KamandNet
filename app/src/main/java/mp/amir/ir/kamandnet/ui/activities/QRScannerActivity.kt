package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_q_r_scanner.*
import kotlinx.coroutines.flow.combine
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.utils.general.toast
import mp.amir.ir.kamandnet.utils.wewi.Constants
import okhttp3.internal.format

class QRScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_scanner)
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
                    toast(result.text)
                    setResult(
                        RESULT_OK,
                        Intent().putExtra(Constants.INTENT_QR_SCANNER_TEXT, result.text)
                    )
                    finish()
                }
            }

            errorCallback = ErrorCallback { exception ->
                exception.message?.let { toast(it) }
            }

        }
    }
}