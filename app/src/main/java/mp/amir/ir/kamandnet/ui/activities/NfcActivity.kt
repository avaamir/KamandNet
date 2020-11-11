package mp.amir.ir.kamandnet.ui.activities

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.utils.general.toast


class NfcActivity : AppCompatActivity() {

    private lateinit var mNfcManager: NfcManager
    private var mNfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        if (hasNFC()) {
            startReading()
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
            if (mNfcAdapter == null) {
                //do something if there are no nfc module on device
            } else {
                onNewIntent(intent)
            }
        } else {
            toast("لطفا NFC دستگاه خود را روشن کنید")
        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }



    private fun hasNFC(): Boolean {
        val adapter = (getSystemService(Context.NFC_SERVICE) as NfcManager).defaultAdapter
        return if (adapter != null && adapter.isEnabled) {
            println("debug:Yes NFC available")
            true
        } else if (adapter != null && !adapter.isEnabled) {
            toast("لطفا NFC دستگاه خود را روشن کنید")
            println("debug:NFC is not enabled.Need to enable by the user.")
            false
        } else {
            toast("این دستگاه NFC ندارد")
            finish()
            false
        }
    }
}