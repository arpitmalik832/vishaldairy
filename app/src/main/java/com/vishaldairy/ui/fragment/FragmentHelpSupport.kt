package com.vishaldairy.ui.fragment

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentHelpSupportBinding
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentHelpSupport: BaseFragment<FragmentHelpSupportBinding>() {

    private var binding: FragmentHelpSupportBinding? = null
    private var viewModel: VMHome? = null

    override val layoutId: Int
        get() = R.layout.fragment_help_support

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this
    }

    override fun initListeners() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(500)

            hideLoading()
            binding?.parentLayout?.visibility = View.VISIBLE
        }

        binding?.faqLayout?.setOnClickListener{
            replaceFragment(
                FragmentFaq(),
                R.id.frame_layout,
                "16",
                true
            )
        }
        binding?.termsLayout?.setOnClickListener{
            (activity as ActivityMain?)?.binding?.snackBarLayout?.let {snackBarLayout ->
                showMessage(
                    "Under Construction",
                    "",
                    snackBarLayout
                )
            }
        }
    }

    override fun bindDataWithViews() {
        initActionBar()
        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it, "Help & Support")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

}

