package com.vishaldairy.ui.activity

import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.base.BaseActivity
import com.vishaldairy.databinding.ActivityLoginBinding
import com.vishaldairy.ui.fragment.FragmentEnterMobile
import com.vishaldairy.viewModel.VMAuth

class ActivityLogin : BaseActivity<ActivityLoginBinding>() {

    var binding: ActivityLoginBinding? = null
    private var viewModel: VMAuth? = null

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = ViewModelProvider(this).get(VMAuth::class.java)
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this
    }

    override fun initListeners() {
    }

    override fun bindDataWithViews() {
        addFragment(
            FragmentEnterMobile(),
            R.id.frame_layout,
            "0"
        )
    }

}

