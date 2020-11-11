package mp.amir.ir.kamandnet.app

import android.app.Activity
import android.app.Application
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDexApplication
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.respository.apiservice.interceptors.NetworkConnectionInterceptor
import mp.amir.ir.kamandnet.respository.persistance.instructiondb.InstructionsRepo
import mp.amir.ir.kamandnet.respository.sharepref.PrefManager

class KamandApplication : MultiDexApplication(), NetworkConnectionInterceptor.INetworkAvailability {

    //Typefaces
    val iransans: Typeface by lazy {
        ResourcesCompat.getFont(this,
            R.font.iransans
        )!!
    }
    val iransansMedium: Typeface by lazy {
        ResourcesCompat.getFont(this,
            R.font.iransans_medium
        )!!
    }
    val iransansLight: Typeface by lazy {
        ResourcesCompat.getFont(this,
            R.font.iransans_light
        )!!
    }
    val belham: Typeface by lazy {
        ResourcesCompat.getFont(this, R.font.belham)!!
    }

    override fun onCreate() {
        super.onCreate()
        PrefManager.init(applicationContext)
        InstructionsRepo.init(applicationContext)
        ApiService.init(this)

        registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            }

            override fun onActivityStarted(p0: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                if (activity is ApiService.OnUnauthorizedListener) {
                    ApiService.setOnUnauthorizedAction(activity)
                }
                if (activity is ApiService.InternetConnectionListener) {
                    ApiService.setInternetConnectionListener(activity)
                }
            }

            override fun onActivityPaused(activity: Activity) {
                if (activity is ApiService.OnUnauthorizedListener) {
                    ApiService.removeUnauthorizedAction(activity)
                }
                if (activity is ApiService.InternetConnectionListener) {
                    ApiService.removeInternetConnectionListener(activity)
                }
            }

            override fun onActivityStopped(p0: Activity) {
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityDestroyed(p0: Activity) {
            }

        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    override fun isInternetAvailable() = isNetworkAvailable()

}