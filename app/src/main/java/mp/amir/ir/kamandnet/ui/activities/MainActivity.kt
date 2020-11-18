package mp.amir.ir.kamandnet.ui.activities

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.activity_main.*
import mp.amir.ir.kamandnet.BuildConfig
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.app.KamandApplication
import mp.amir.ir.kamandnet.databinding.ActivityMainBinding
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.models.UpdateResponse
import mp.amir.ir.kamandnet.models.User
import mp.amir.ir.kamandnet.respository.UserConfigs
import mp.amir.ir.kamandnet.respository.apiservice.ApiService
import mp.amir.ir.kamandnet.respository.sharepref.PrefManager
import mp.amir.ir.kamandnet.ui.adapter.InstructionsAdapter
import mp.amir.ir.kamandnet.ui.customs.animations.closeReveal
import mp.amir.ir.kamandnet.ui.customs.animations.startReveal
import mp.amir.ir.kamandnet.ui.customs.dialogs.LocationPermissionDialog
import mp.amir.ir.kamandnet.ui.dialogs.NoNetworkDialog
import mp.amir.ir.kamandnet.utils.general.*
import mp.amir.ir.kamandnet.utils.kamand.Constants
import mp.amir.ir.kamandnet.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity(), InstructionsAdapter.Interaction,
    PermissionHelper.Interactions, ApiService.InternetConnectionListener,
    ApiService.OnUnauthorizedListener {

    companion object {
        private const val REQ_GO_TO_SETTINGS_PERMISSION = 14
    }

    private val mAdapter = InstructionsAdapter(this)
    private var isSearchShown = false

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mBinding: ActivityMainBinding

    private val permissionHelper = PermissionHelper(
        arrayListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.NFC
        ), this, this
    )

    // drawer
    private lateinit var mDrawer: Drawer
    private lateinit var tvNameDrawer: TextView
    private lateinit var ivProfile: ImageView
    private val drawerHome by lazy {
        PrimaryDrawerItem()
            .withName("خانه")
            .withIcon(R.drawable.ic_homepage_)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                mDrawer.closeDrawer()
                true
            }
    }
    private val drawerShowProfile by lazy {
        PrimaryDrawerItem()
            .withName("نمایش پروفایل")
            .withIcon(R.drawable.ic_person_colored)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                toast("not yet implemeted")
                true
            }
    }
    private val drawerSettings by lazy {
        PrimaryDrawerItem()
            .withName("تنظیمات")
            .withIcon(R.drawable.ic_settings)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
    }
    private val drawerMessages by lazy {
        PrimaryDrawerItem()
            .withName("پیام ها")
            .withIcon(R.drawable.ic_message)
            //.withBadge(StringHolder(viewModel.messageCount.value?.toString() ?: "0"))
            //.withBadgeStyle(BadgeStyle().withTextColor(ContextCompat.getColor(this, R.color.red)))
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                val intent = Intent(this@MainActivity, MessageActivity::class.java)
                startActivity(intent)
                true
            }
    }
    private val drawerAboutUs by lazy {
        PrimaryDrawerItem()
            .withName("درباره ما")
            .withIcon(R.drawable.ic_drawer_about_us)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                startActivity(Intent(this@MainActivity, AboutUsActivity::class.java))
                //todo override pending transition
                true
            }
    }
    private val drawerLogout by lazy {
        PrimaryDrawerItem()
            .withName("خروج از حساب کاربری")
            .withIcon(R.drawable.ic_darwer_logout)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                mDrawer.setSelection(drawerHome)
                mDrawer.closeDrawer()
                Handler().postDelayed({
                    alert(
                        "خروج از حساب",
                        "آیا از حساب کاربری خود خارج میشوید؟",
                        "بله، خارج میشوم",
                        "خیر",
                        true,
                        null
                    ) {
                        viewModel.logout()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }, 10)
                true
            } // خروج از حساب کاربری
    }

    //Typefaces
    private val iransansMedium: Typeface get() = (application as KamandApplication).iransansMedium
    private val iransansLight: Typeface get() = (application as KamandApplication).iransansLight
    private val belham: Typeface get() = (application as KamandApplication).belham

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (false) {
            startActivity(Intent(this, TestActivity::class.java))
            finish()
            return
        }

        if(PrefManager.baseURL == null) {
            startActivity(Intent(this, SettingsActivity::class.java).apply {
                putExtra(Constants.INTENT_SETTINGS_DOMAIN_NOT_INIT, true)
            })
            finish()
            return
        }

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
        initDrawer(viewModel.user)
        subscribeObservers()

        viewModel.getInstructionsFromServer()
        permissionHelper.checkPermission()
    }

    override fun onResume() {
        super.onResume()
        if (::mDrawer.isInitialized) {
            if (mDrawer.isDrawerOpen) {
                mDrawer.closeDrawer()
            }
            mDrawer.setSelection(drawerHome)
        }
    }

    override fun onBackPressed() {
        when {
            ::mDrawer.isInitialized && mDrawer.isDrawerOpen -> {
                mDrawer.closeDrawer()
                mDrawer.setSelection(drawerHome)
            }
            isSearchShown -> {
                viewModel.filterList(null)
                closeSearchBar()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun initViews() {
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getInstructionsFromServer()
        }

        mBinding.recyclerInstruction.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.recyclerInstruction.adapter = mAdapter

        mBinding.btnSync.setOnClickListener {
            viewModel.sync()
        }
    }

    private fun subscribeObservers() {

        subscribeNetworkStateChangeListener {
            if (it) {
                if (permissionHelper.arePermissionsGranted()) {
                    viewModel.checkUpdates()
                }
            }
        }


        viewModel.submitInstructionResponse.observe(this, {
            if (it != null) {
                if (it.isSucceed) {
                    toast(it.message)
                } else {
                    toast(it.message)
                }
            } else {
                toast("خطا در ارسال اطلاعات به سرور. لطفا وضعیت شبکه خود را بررسی کنید")
            }
        })

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
                        if (isNetworkAvailable()) {
                            viewModel.checkUpdates()
                        }
                        //TODO show proper message
                    }
                } else {
                    if (isNetworkAvailable()) {
                        viewModel.checkUpdates()
                    }
                    //TODO show proper message
                }
            }
        })


        viewModel.getInstructionsFromServerResponse.observe(this, {
            if (mBinding.progressBar.visibility != View.GONE)
                mBinding.progressBar.visibility = View.GONE
            if (mBinding.swipeRefreshLayout.isRefreshing)
                mBinding.swipeRefreshLayout.isRefreshing = false
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

        ivNavigate.setOnClickListener {
            if (!mDrawer.isDrawerOpen) {
                mDrawer.openDrawer()
            }
            //viewModel.updateUserProfile()
        }

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


    private fun initDrawer(user: User) {
        val headerResult: AccountHeader =
            AccountHeaderBuilder()
                .withActivity(this)
                .withAccountHeader(R.layout.material_drawer_header_logged_in)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.toolbar_gradient)
                .withNameTypeface(iransansLight)
                .withTextColor(ContextCompat.getColor(this, R.color.white))
                .build()


        val drawerProfile = headerResult.view.findViewById<RelativeLayout>(R.id.text_layout)
        tvNameDrawer = headerResult.view.findViewById(R.id.user_fullName_text)
        tvNameDrawer.typeface = iransansMedium

        val showProfileText =
            headerResult.view.findViewById<TextView>(R.id.show_profile_text)
        showProfileText.typeface = iransansLight

        ivProfile = headerResult.view.findViewById(R.id.main_activity_profile_image)
        drawerProfile.setOnClickListener {
            toast("not yet implemented")
        }
        tvNameDrawer.text = user.name
        loadProfilePic(ivProfile, user.profilePic)

        val headerAppName = headerResult.view.findViewById<TextView>(R.id.header_app_name)
        headerAppName.typeface = belham
        //
        mDrawer = DrawerBuilder()
            .withDrawerGravity(Gravity.END)
            .withActivity(this)
            .withTranslucentStatusBar(true)
            .withActionBarDrawerToggle(true)
            .withAccountHeader(headerResult)
            .withDisplayBelowStatusBar(false)
            .withSelectedItem(-1)
            .build()

        mDrawer.addItems(
            drawerHome,
            DividerDrawerItem(),
            drawerShowProfile,
            DividerDrawerItem(),
            drawerMessages,
            DividerDrawerItem(),
            drawerSettings,
            DividerDrawerItem(),
            drawerAboutUs,
            drawerLogout
        )

    }

    override fun onInstructionItemClicked(item: Instruction) {
        startActivity(
            Intent(this, InstructionActivity::class.java).apply {
                putParcelableExtra(Constants.INTENT_INSTRUCTION_DATA, item)
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
        if (isNetworkAvailable())
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

    override fun onUnauthorizedAction(event: Event<Unit>) {
        event.getEventIfNotHandled()?.let {
            toast("فرد دیگری با حساب کاربری شما وارد شده است. باید دوباره وارد شوید")
            finish()
        }
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }
}