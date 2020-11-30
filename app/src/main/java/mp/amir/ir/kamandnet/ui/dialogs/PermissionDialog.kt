package mp.amir.ir.kamandnet.ui.dialogs

import android.content.Context
import kotlinx.android.synthetic.main.layout_permission_dialog.*
import mp.amir.ir.kamandnet.R

class PermissionDialog(
    context: Context,
    themeResId: Int,
    private val onGranted: (Boolean, PermissionDialog) -> Unit
) : MyBaseDialog(
    context, themeResId,
    R.layout.layout_permission_dialog
) {

    init {
        setCancelable(false)
    }

    override fun initViews() {
        btn_accept.setOnClickListener { onGranted(true, this) }
        btn_denied.setOnClickListener { onGranted(false, this) }
    }
}