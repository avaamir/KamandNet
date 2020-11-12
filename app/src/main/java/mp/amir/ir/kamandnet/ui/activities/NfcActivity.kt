package mp.amir.ir.kamandnet.ui.activities

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.utils.general.toast
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import java.util.*


class NfcActivity : AppCompatActivity() {


    private val shouldRead = true

    private var mNfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        if (hasNFC()) {
            // startReading()
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

    private fun readNFC(intent: Intent?) {
        val parcels = intent?.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        if (parcels != null && parcels.isNotEmpty()) {
            readTextFromTag(parcels[0] as NdefMessage)
        } else {
            toast("no NDEF messages found!")
        }
    }

    private fun readTextFromTag(ndefMessage: NdefMessage) {
        val records = ndefMessage.records
        if (records != null && records.isNotEmpty()) {
            val record = records[0]
            //val tagContent = getTextFromNdefRecord(record)
            val tagContent = String(record.payload)
            toast(tagContent) //TODO this is the content
        } else {
            toast("no NDEF records found!")
        }
    }


    private fun writeNFC(intent: Intent?) {
        if (intent?.hasExtra(NfcAdapter.EXTRA_TAG) == true) {
            toast("nfc intent received")

            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val ndefMessage = createNdefMessage("test content")
            if (tag != null)
                writeNdefMessage(tag, ndefMessage)
            else {
                toast("tag is null")
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (shouldRead) {
            //read
            readNFC(intent)
        } else {
            //write
            writeNFC(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        enableForegroundDispatch()
    }

    override fun onPause() {
        disableForegroundDispatch()
        super.onPause()
    }

    private fun enableForegroundDispatch() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val filters = arrayOf<IntentFilter>()

        mNfcAdapter!!.enableForegroundDispatch(
            this,
            pendingIntent,
            filters,
            null
        )
    }

    private fun disableForegroundDispatch() {
        mNfcAdapter!!.disableForegroundDispatch(this)
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


    private fun formatTag(tag: Tag, ndefMessage: NdefMessage) {
        try {
            val ndefFormatable = NdefFormatable.get(tag)
            if (ndefFormatable == null) {
                toast("Tag is not ndef formatable")
                return
            }
            ndefFormatable.connect()
            ndefFormatable.format(ndefMessage)
            ndefFormatable.close()
        } catch (ex: Exception) {
            toast(ex.message ?: "خطایی به وجود آمد")
        }
    }

    private fun writeNdefMessage(tag: Tag, message: NdefMessage) {
        try {
            val ndef = Ndef.get(tag)
            if (ndef == null) {
                //format tag with the ndef format and write the message
                formatTag(tag, message)
            } else {
                ndef.connect()
                if (ndef.isWritable) {
                    ndef.writeNdefMessage(message)
                    toast("tag written")
                } else {
                    toast("Tag is not writable!")
                }
                ndef.close()
            }
        } catch (ex: Exception) {
            toast(ex.message ?: "خطا")
        }
    }

    private fun createNdefMessage(content: String): NdefMessage {
        val ndefRecord = createTextRecord(content)
        return NdefMessage(arrayOf(ndefRecord))
    }

    private fun createTextRecord(content: String): NdefRecord? {
        try {
            val language = Locale.getDefault().language.toByteArray()

            val text = content.toByteArray()

            val payload = ByteArrayOutputStream(1 + language.size + text.size).apply {
                write((language.size and 0x1f))
                write(language, 0, language.size)
                write(text, 0, text.size)
            }
            return NdefRecord(
                NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                byteArrayOf(0), payload.toByteArray()
            )

        } catch (ex: UnsupportedEncodingException) {
            println("debug: ${ex.message}")
        }
        return null
    }

}