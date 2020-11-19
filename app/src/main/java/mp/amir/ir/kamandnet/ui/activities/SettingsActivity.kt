package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ActivitySettingsBinding
import mp.amir.ir.kamandnet.respository.sharepref.PrefManager
import mp.amir.ir.kamandnet.utils.general.alert
import mp.amir.ir.kamandnet.utils.general.toast
import mp.amir.ir.kamandnet.utils.kamand.Constants

class SettingsActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySettingsBinding
    private var domainNotInitialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        domainNotInitialized =
            intent.getBooleanExtra(Constants.INTENT_SETTINGS_DOMAIN_NOT_INIT, false)
        initViews()
    }

    private fun initViews() {
        PrefManager.domain?.let { mBinding.etDomain.setText(it) }


        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.ivClearDomain.setOnClickListener {
            mBinding.etDomain.text.clear()
        }


        mBinding.btnSave.setOnClickListener {
            var domain = mBinding.etDomain.text.toString().trim()
            if (domain.isEmpty()) {
                toast(getString(R.string.error_domain_not_set))
                return@setOnClickListener
            }


            if (!domain.startsWith("http")) {
                toast("دامنه باید با http:// یا https:// شروع شود")
                return@setOnClickListener
            }
            if (!domain.endsWith("/")) {
                domain += "/"
            }
            PrefManager.saveDomain(domain)
            if (domainNotInitialized) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }

    override fun onBackPressed() {
        val domain = mBinding.etDomain.text.toString().trim()
        if (domain != PrefManager.domain) {
            alert(
                "توجه",
                "تغییرات ذخیره شود؟",
                "بله",
                "خیر",
                true,
                {
                    if (domainNotInitialized) {
                        toast(getString(R.string.error_domain_not_set))
                        finish()
                    } else
                        super.onBackPressed()
                },
            ) {
                PrefManager.saveDomain(domain)
                if (domainNotInitialized) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else
                    super.onBackPressed()
            }
        } else
            super.onBackPressed()
    }

}