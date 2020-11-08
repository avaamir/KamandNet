package mp.amir.ir.kamandnet.ui.dialogs

import android.app.Activity
import kotlinx.android.synthetic.main.dialog_nfc_qr.*
import mp.amir.ir.kamandnet.R

class QRorNFCDialog(
    activity: Activity,
    themeResId: Int,
    private val interactions: Interactions
) :
    MyBaseDialog(
        activity, themeResId,
        R.layout.dialog_nfc_qr
    ) {


    override fun initViews() {
        btnNFC.setOnClickListener {
            interactions.onNFCClicked()
            dismiss()
        }
        btnQR.setOnClickListener {
            interactions.onQRClicked()
            dismiss()
        }
    }



    interface Interactions {
        fun onQRClicked()
        fun onNFCClicked()
    }
}