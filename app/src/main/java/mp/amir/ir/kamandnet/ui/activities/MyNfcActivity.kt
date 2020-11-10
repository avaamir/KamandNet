package mp.amir.ir.kamandnet.ui.activities

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.SparseArray
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mp.amir.ir.kamandnet.R


class MyNfcActivity : AppCompatActivity() {

    private var pendingIntent: PendingIntent? = null
    private lateinit var mIntentFilters: Array<IntentFilter>
    private lateinit var mTechLists: Array<Array<String>>
    private var mNfcAdapter: NfcAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )
        mIntentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
        mTechLists = arrayOf(
            arrayOf(Ndef::class.java.name),
            arrayOf(NdefFormatable::class.java.name)
        )


    }

    override fun onResume() {
        super.onResume()
        mNfcAdapter?.enableForegroundDispatch(this, pendingIntent, mIntentFilters, mTechLists);
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val res: SparseArray<String> = NfcReadUtilityImpl().readFromTagWithSparseArray(intent)
        for (i in 0 until res.size()) {
            Toast.makeText(this, res.valueAt(i), Toast.LENGTH_SHORT).show()
        }
    }


}