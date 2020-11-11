package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ActivityLoginBinding
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.ui.dialogs.NoNetworkDialog
import mp.amir.ir.kamandnet.utils.general.*
import mp.amir.ir.kamandnet.utils.kamand.Constants
import mp.amir.ir.kamandnet.viewmodels.LoginActivityViewModel

class LoginActivity : AppCompatActivity(), ApiService.InternetConnectionListener {
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)


        initViews()
        subscribeObservers()
    }

    private fun initViews() {
        mBinding.btnLogin.setOnClickListener {
            val username = mBinding.etUsername.text.trim().toString()
            val password = mBinding.etPassword.text.trim().toString()

            when {
                username.isEmpty() -> toast("نام کاربری خود را وارد کنید")
                password.isEmpty() -> toast("گذرواژه خود را وارد کنید")
                else -> {
                    viewModel.login(username, password)
                    mBinding.btnLogin.showProgressBar(true)
                }
            }


        }
    }

    private fun subscribeObservers() {
        viewModel.loginResponse.observe(this, {
            mBinding.btnLogin.showProgressBar(false)
            if (it != null) {
                if (it.isSucceed) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    toast(it.message)
                }
            } else {
                snack(Constants.SERVER_ERROR) {
                    viewModel.loginAgain()
                    mBinding.btnLogin.showProgressBar(true)
                }
            }
        })
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }
}