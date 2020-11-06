package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ActivityLoginBinding
import mp.amir.ir.kamandnet.ui.activities.customs.animations.closeReveal
import mp.amir.ir.kamandnet.ui.activities.customs.animations.startReveal
import mp.amir.ir.kamandnet.utils.general.*
import mp.amir.ir.kamandnet.utils.wewi.Constants
import mp.amir.ir.kamandnet.viewmodels.LoginActivityViewModel

class LoginActivity : AppCompatActivity() {
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
                }
            }


        }
    }

    private fun subscribeObservers() {
        viewModel.loginResponse.observe(this, {
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
                }
            }
        })
    }
}