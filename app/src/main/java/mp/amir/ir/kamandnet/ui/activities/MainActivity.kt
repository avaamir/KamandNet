package mp.amir.ir.kamandnet.ui.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import mp.amir.ir.kamandnet.BuildConfig
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ActivityMainBinding
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.UpdateResponse
import mp.amir.ir.kamandnet.respository.UserConfigs
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.ui.adapter.InstructionsAdapter
import mp.amir.ir.kamandnet.ui.customs.animations.closeReveal
import mp.amir.ir.kamandnet.ui.customs.animations.startReveal
import mp.amir.ir.kamandnet.ui.customs.dialogs.LocationPermissionDialog
import mp.amir.ir.kamandnet.utils.general.*
import mp.amir.ir.kamandnet.utils.wewi.Constants
import mp.amir.ir.kamandnet.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity(), InstructionsAdapter.Interaction,
    PermissionHelper.Interactions {

    companion object {
        private const val REQ_GO_TO_SETTINGS_PERMISSION = 14


    }

    private val mAdapter = InstructionsAdapter(this)
    private var isSearchShown = false

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mBinding: ActivityMainBinding

    private val permissionHelper = PermissionHelper( arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ), this,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!UserConfigs.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        initViews()
        initToolbarButtons()
        subscribeObservers()

        viewModel.getInstructionsFromServer()
        permissionHelper.checkPermission()
    }

    override fun onBackPressed() {
        if (isSearchShown) {
            closeSearchBar()
        } else {
            super.onBackPressed()
        }
    }

    private fun initViews() {
        mBinding.recyclerInstruction.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.recyclerInstruction.adapter = mAdapter
    }

    private fun subscribeObservers() {

        subscribeNetworkStateChangeListener {
            if (it) {
                //TODO vaghti net nadare ama dastura load shodan faghat bala ye alamat biad ke yaani net nadari va save shodan flow ha to server bere tu halat pending
                if (permissionHelper.arePermissionsGranted()) {
                    viewModel.checkUpdates()
                }
            } else {
                //TODO
            }
        }



        viewModel.checkUpdateResponse.observe(this, { event ->
            event.getEventIfNotHandled()?.let {
                if (it.entity !== UpdateResponse.NoResponse) {
                    if (it.isSucceed) {
                        if (it.entity!!.version > BuildConfig.VERSION_CODE) {
                            startActivity(Intent(this, UpdateActivity::class.java).also { intent ->
                                intent.putParcelableExtra(
                                    Constants.INTENT_UPDATE_ACTIVITY_UPDATE_RESPONSE,
                                    it.entity
                                )
                            })
                            finish()
                        }
                    } else {
                        if (ApiService.isNetworkAvailable()) {
                            viewModel.checkUpdates()
                        }
                        //TODO show proper message
                    }
                } else {
                    if (ApiService.isNetworkAvailable()) {
                        viewModel.checkUpdates()
                    }
                    //TODO show proper message
                }
            }
        })


        viewModel.getInstructionsFromServerResponse.observe(this, {
            mBinding.progressBar.visibility = View.GONE
            if (it != null) {
                if (it.isSucceed) {
                    //TODO test for checking db
                    //mAdapter.submitList(it.entity)
                    if (it.entity!!.isEmpty()) {
                        toast("شما دستور کار دیگری ندارید")
                    }
                } else {
                    toast(it.message)
                }
            } else {
                snack(Constants.SERVER_ERROR) {
                    viewModel.getInstructionsFromServer()
                }
            }
        })

        viewModel.instructions.observe(this, {
            if (mBinding.progressBar.visibility != View.GONE)
                mBinding.progressBar.visibility = View.GONE
            mAdapter.submitList(it)
        })
    }

    private fun initToolbarButtons() {

        /*ivNavigate.setOnClickListener {
            if (!mDrawer.isDrawerOpen) {
                mDrawer.openDrawer()
            }
            viewModel.updateUserProfile()
        }*/

        ivSearch.setOnClickListener {
            isSearchShown = true
            frame_toolbar_buttons.visibility = View.INVISIBLE
            frame_search.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startReveal(frame_search) {
                    if (etSearch.requestFocus())
                        showSoftKeyboard(etSearch)
                }
            } else {
                if (etSearch.requestFocus())
                    showSoftKeyboard(etSearch)
            }
        }

        ivClose.setOnClickListener {  //TODO closeReveal kar nemikonad??
            viewModel.filterList(null)
            closeSearchBar()
        }


        etSearch.addTextChangedListener(object : OptimizedSearchTextWatcher() {
            override fun itsSearchTime(s: Editable) {
                if (s.length > 2) {
                    viewModel.filterList(s.toString().clearString())
                    //TODO add animation
                    /*mBinding.animationView.visibility = View.GONE
                    mBinding.recyclerSearchAddress.visibility = View.GONE
                    mBinding.progressBar.visibility = View.VISIBLE*/
                }
            }

        })
    }

    private fun closeSearchBar() {
        isSearchShown = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            closeReveal(frame_search) {
                frame_search.visibility = View.INVISIBLE
            }
        } else {
            frame_search.visibility = View.INVISIBLE
        }
        frame_toolbar_buttons.visibility = View.VISIBLE
        hideSoftKeyboard()
        etSearch.text.clear()
    }

    override fun onInstructionItemClicked(item: Instruction) {
        startActivity(
            Intent(this, InstructionActivity::class.java).apply {
                putParcelableExtra(Constants.INTENT_INSTRUCTION_ACTIVITY_DATA, item)
            }
        )
    }

    override fun beforeRequestPermissionsDialogMessage(
        notGrantedPermissions: ArrayList<String>,
        permissionRequesterFunction: () -> Unit
    ) {
        notGrantedPermissions.forEach {
            println("debug: $it not granted, request permissions now") //can show a dialog then request
        }
        LocationPermissionDialog(this, R.style.my_alert_dialog) { isGranted, dialog ->
            if (isGranted) {
                permissionRequesterFunction.invoke()
                dialog.dismiss()
            } else {
                toast("اجازه دسترسی داده نشد")
                finish()
            }
        }.show()
    }

    override fun onDeniedWithNeverAskAgain(permission: String) {
        println("debug:show dialog:$permission -> Go to Settings")

        alert(
            title = "دسترسی",
            message = "برنامه برای ادامه فعالیت خود به اجازه شما نیاز دارد. لطفا در تنظیمات برنامه دسترسی ها را درست کنید",
            positiveButtonText = "رفتن به تنظیمات",
            negativeButtonText = "بستن برنامه",
            isCancelable = false,
            onNegativeClicked = { finish() }
        ) {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                addCategory(Intent.CATEGORY_DEFAULT)
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }
            startActivityForResult(intent, REQ_GO_TO_SETTINGS_PERMISSION)
        }


    }

    override fun onPermissionsGranted() {
        if (ApiService.isNetworkAvailable())
            viewModel.checkUpdates()
    }

    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
        toast("اجازه دسترسی داده نشد")
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHelper.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_GO_TO_SETTINGS_PERMISSION) {
            permissionHelper.checkPermission()
        }
    }





}