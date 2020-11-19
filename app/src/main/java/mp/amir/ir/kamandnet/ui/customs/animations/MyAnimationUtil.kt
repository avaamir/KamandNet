package mp.amir.ir.kamandnet.ui.customs.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import androidx.annotation.RequiresApi
import kotlin.math.hypot

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun startReveal(view: View, onAnimationEnd: () -> Unit) {
    val cx = view.width / 2
    val cy = view.height / 2
    val finalRadius = hypot(cx.toFloat(), cy.toFloat())
    val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            onAnimationEnd()
        }
    })
    anim.start()
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun closeReveal(view: View, onAnimationEnd: () -> Unit) {
    val cx = view.width / 2
    val cy = view.height / 2
    val initialRadius = hypot(cx.toFloat(), cy.toFloat())
    val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            onAnimationEnd()
        }
    })
    anim.start()
}
