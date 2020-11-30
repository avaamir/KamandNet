package mp.amir.ir.kamandnet.utils.general

import android.app.Activity
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import mp.amir.ir.kamandnet.R

fun Activity.toast(text: String, isLongLength: Boolean = true) {
    //TOAST
    val layout = layoutInflater
        .inflate(
            R.layout.view_my_toast_layout,
            findViewById(R.id.custom_toast_container)
        )
    val textView = layout.findViewById<TextView>(R.id.text_in_my_toast)
    textView.text = text
    val toast = Toast.makeText(
        applicationContext,
        "",
        if (isLongLength) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    )
    toast.setGravity(toast.gravity, toast.xOffset, toast.yOffset + 50)
    toast.view = layout
    toast.show()
}


fun Fragment.toast(text: String, isLongLength: Boolean = false) {
    activity?.toast(text, isLongLength)
}