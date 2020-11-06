package mp.amir.ir.kamandnet.app

import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDexApplication
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.respository.sharepref.PrefManager

class KamandApplication : MultiDexApplication() {

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
        PrefManager.setContext(applicationContext)


    }

}