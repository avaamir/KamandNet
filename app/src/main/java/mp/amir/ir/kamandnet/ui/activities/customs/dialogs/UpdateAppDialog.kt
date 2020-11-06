package mp.amir.ir.kamandnet.ui.activities.customs.dialogs

import android.content.Context
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_update_app_info.*
import mp.amir.ir.kamandnet.R

class UpdateAppDialog(
    context: Context,
    themeResId: Int,
    private val isForce: Boolean,
    private val onGranted: (Boolean, UpdateAppDialog) -> Unit
) : MyBaseDialog(
    context, themeResId,
    R.layout.layout_update_app_info
) {

    init {
        setCancelable(!isForce)
    }

    override fun initViews() {
        val tvMessage = findViewById<TextView>(R.id.textView27)

        if(isForce) {
            tvMessage.text = "اپلیکیشن نیاز به آپدیت دارد. لطفا آپدیت کنید."
        }

        btn_denied.setOnClickListener { onGranted(false, this) }
        btn_accept.setOnClickListener { onGranted(true, this) }
    }
}