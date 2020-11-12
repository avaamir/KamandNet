package mp.amir.ir.kamandnet.ui.dialogs

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_no_network_dialog.*
import mp.amir.ir.kamandnet.R

class NoNetworkDialog(
    context: Context,
    themeResId: Int
) : MyBaseDialog(
    context, themeResId,
    R.layout.layout_no_network_dialog
) {
    override fun initViews() {
        frame_check_settings.setOnClickListener {
            try {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } catch (ex: Exception) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
                //TODO add intent go to data setting if wifi settings is not available
            }

            dismiss()
        }
    }
}