package mp.amir.ir.kamandnet.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import mp.amir.ir.kamandnet.R
import mp.amir.ir.kamandnet.databinding.ActivityMainBinding
import mp.amir.ir.kamandnet.models.Instruction
import mp.amir.ir.kamandnet.respository.UserConfigs
import mp.amir.ir.kamandnet.ui.activities.customs.animations.closeReveal
import mp.amir.ir.kamandnet.ui.activities.customs.animations.startReveal
import mp.amir.ir.kamandnet.ui.adapter.InstructionsAdapter
import mp.amir.ir.kamandnet.utils.general.*
import mp.amir.ir.kamandnet.utils.wewi.Constants
import mp.amir.ir.kamandnet.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity(), InstructionsAdapter.Interaction {

    private val mAdapter = InstructionsAdapter(this)
    private var isSearchShown = false

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mBinding: ActivityMainBinding

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

        viewModel.getInstructions()
    }

    private fun initViews() {
        mBinding.recyclerInstruction.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.recyclerInstruction.adapter = mAdapter
    }

    private fun subscribeObservers() {
        viewModel.instructions.observe(this, {
            if (it != null) {
                if (it.isSucceed) {
                    mAdapter.submitList(it.entity)
                } else {
                    toast(it.message)
                }
            } else {
                snack(Constants.SERVER_ERROR) {
                    viewModel.getInstructions()
                }
            }
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
            closeSearchBar()
        }


        etSearch.addTextChangedListener(object : OptimizedSearchTextWatcher() {
            override fun itsSearchTime(s: Editable) {
                if (s.length > 2) {
                    viewModel.filterList(s.toString().trim())
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
        TODO("Not yet implemented")
    }


}