package mp.amir.ir.kamandnet.app

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDexApplication
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.respository.apiservice.interceptors.NetworkConnectionInterceptor
import mp.amir.ir.kamandnet.respository.persistance.instructiondb.InstructionsRepo
import mp.amir.ir.kamandnet.respository.sharepref.PrefManager
import mp.amir.ir.kamandnet.utils.general.isNetworkAvailable

class KamandApplication : MultiDexApplication(), NetworkConnectionInterceptor.INetworkAvailability {

    //Typefaces
    val iransans: Typeface by lazy {
        ResourcesCompat.getFont(
            this,
            R.font.iransans
        )!!
    }
    val iransansMedium: Typeface by lazy {
        ResourcesCompat.getFont(
            this,
            R.font.iransans_medium
        )!!
    }
    val iransansLight: Typeface by lazy {
        ResourcesCompat.getFont(
            this,
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

        ApiService.init(domain = PrefManager.domain ?: "", iNetworkAvailability = this)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
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

    override fun isInternetAvailable() = isNetworkAvailable()

}