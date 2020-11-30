package mp.amir.ir.kamandnet.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.android.synthetic.main.layout_my_progress_dialog_intermediate.*
import mp.amir.ir.kamandnet.R

class MyProgressDialog(
    context: Context,
    themeResId: Int,
    private val isIntermediate: Boolean = false
) : MyBaseDialog(
    context, themeResId,
    if (isIntermediate) R.layout.layout_my_progress_dialog_intermediate else R.layout.layout_my_progress_dialog
) {
    override fun initViews() {
        setCancelable(false)
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int) {
        if (!isIntermediate)
            throw IllegalStateException("can not set progress to non intermediate progress bar")
        progressBar.progress = progress
        tvProgress.text = "%$progress"

    }
}