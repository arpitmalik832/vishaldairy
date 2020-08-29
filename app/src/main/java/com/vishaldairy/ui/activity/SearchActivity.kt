package com.vishaldairy.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.base.BaseActivity
import com.vishaldairy.databinding.ActivityLoginBinding
import com.vishaldairy.databinding.ActivitySearchBinding
import com.vishaldairy.ui.fragment.FragmentEnterMobile
import com.vishaldairy.viewModel.VMAuth
import com.vishaldairy.viewModel.VMHome

class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    var binding: ActivitySearchBinding? = null
    private var viewModel: VMHome? = null

    override val layoutId: Int
        get() = R.layout.activity_search

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = ViewModelProvider(this).get(VMHome::class.java)
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this
    }

    override fun initListeners() {
        viewModel?.searchKey?.observe(this, Observer {

        })
    }

    override fun bindDataWithViews() {
    }

}

