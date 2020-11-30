package mp.amir.ir.kamandnet.utils.general

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import mp.amir.ir.kamandnet.app.receivers.NetworkStateReceiverLiveData

fun FragmentActivity.subscribeNetworkStateChangeListener(onStateChanged: (Boolean) -> Unit) {
    NetworkStateReceiverLiveData(this).observe(this, Observer {
        onStateChanged(it)
    })
}