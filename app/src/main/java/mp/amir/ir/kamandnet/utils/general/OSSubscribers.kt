package mp.amir.ir.kamandnet.utils.general

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import mp.amir.ir.kamandnet.app.receivers.GpsStateReceiverLiveData
import mp.amir.ir.kamandnet.app.receivers.NetworkStateReceiverLiveData
import mp.amir.ir.kamandnet.app.receivers.SignalStrengthReceiverLiveData

fun FragmentActivity.subscribeNetworkStateChangeListener(onStateChanged: (Boolean) -> Unit) {
    NetworkStateReceiverLiveData(this).observe(this, Observer {
        onStateChanged(it)
    })
}

fun FragmentActivity.subscribeGpsStateChangeListener(onStateChanged: (Boolean) -> Unit) {
    GpsStateReceiverLiveData(this).observe(this, Observer {
        onStateChanged(it)
    })
}


fun FragmentActivity.subscribeSignalStrengthChangeListener(
    forEver: Boolean = false,
    onSignalLevelChanged: (Int) -> Unit
) =
    if (!forEver) {
        SignalStrengthReceiverLiveData(this).also {
            it.observe(this, Observer { signalLevel ->
                onSignalLevelChanged(signalLevel)
            })
        }
    } else {
        SignalStrengthReceiverLiveData(this).also {
            it.observeForever { signalLevel ->
                onSignalLevelChanged(signalLevel)
            }
        }
    }