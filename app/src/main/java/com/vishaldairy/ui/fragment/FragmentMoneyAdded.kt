package com.vishaldairy.ui.fragment

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentMoneyAddedBinding
import com.vishaldairy.viewModel.VMHome

class FragmentMoneyAdded: BaseFragment<FragmentMoneyAddedBinding>() {

    private var binding: FragmentMoneyAddedBinding? = null
    private var viewModel: VMHome? = null

    override val layoutId: Int
        get() = R.layout.fragment_money_added

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
    }

    override fun bindDataWithViews() {
        initActionBar()
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it, "Go Back")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

}

