package mp.amir.ir.kamandnet.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_update.*
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.models.UpdateResponse
import mp.amir.ir.kamandnet.ui.dialogs.MyProgressDialog
import mp.amir.ir.kamandnet.utils.general.AppUpdater
import mp.amir.ir.kamandnet.utils.general.alert
import mp.amir.ir.kamandnet.utils.general.toast
import mp.amir.ir.kamandnet.utils.kamand.Constants

class UpdateActivity : AppCompatActivity(), AppUpdater.Interactions {
    private lateinit var dialog: MyProgressDialog

    private var isDownloadUpdateFinished = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            updateActivityRootFrame.post {
                intent.getParcelableExtra<UpdateResponse>(Constants.INTENT_UPDATE_ACTIVITY_UPDATE_RESPONSE)!!
                    .let {
                        alert(
                            "بروزرسانی",
                            "نسخه جدیدی از برنامه موجود میباشد. برای ادامه کار نیاز به بروزرسانی دارید. آیا ادامه میدهید؟",
                            "ادامه",
                            "خروج",
                            false,
                            { finish() }
                        ) {
                            AppUpdater(
                                this,
                                it.link,
                                //"$cacheDir/FasterMixer.apk",
                                "${getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/FasterMixer.apk".also {
                                    println(
                                        "debux:$it"
                                    )
                                },
                                it.version,
                                this
                            ).startIfNeeded()
                        }
                    }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (isDownloadUpdateFinished) {
            toast("باید بروزرسانی کنید")
            finish()
        }
    }


    override fun onDownloadStarted() {
        dialog = MyProgressDialog(this, R.style.my_alert_dialog, true)
        dialog.show()
    }

    override fun onProgressUpdate(progress: Int) {
        if (progress == 100) {
            isDownloadUpdateFinished = true
        }
        dialog.setProgress(progress)
    }

    override fun onDownloadCancelled(message: String) {
        dialog.dismiss()
        toast(message, true)
    }

    override fun onServerError(serverCode: Int) {
        println("debug: UI : Server error")
        dialog.dismiss()
        toast("متاسفانه خطایی در سرور پیش آمده است. لطفا دقایقی دیگر تلاش کنید")
        finish()
    }
}