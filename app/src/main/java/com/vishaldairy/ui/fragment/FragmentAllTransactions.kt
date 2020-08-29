package com.vishaldairy.ui.fragment

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentAllTransactionsBinding
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentAllTransactions: BaseFragment<FragmentAllTransactionsBinding>() {

    private var binding: FragmentAllTransactionsBinding? = null
    private var viewModel: VMHome? = null

    override val layoutId: Int
        get() = R.layout.fragment_all_transactions

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

        binding?.walletOption?.setOnClickListener {
            context?.let {context ->
                binding?.walletOption?.background = AppCompatResources.getDrawable(
                    context,
                    R.drawable.background_color_primary_fill_with_corners_10dp)
                binding?.walletOption?.setTextColor(Color.WHITE)
                binding?.myCreditsOption?.background = AppCompatResources.getDrawable(
                    context,
                    R.drawable.background_efefef_color_fill_with_corners_10dp)
                binding?.myCreditsOption?.setTextColor(Color.BLACK)
            }
        }

        binding?.myCreditsOption?.setOnClickListener {
            context?.let {context ->
                binding?.walletOption?.background = AppCompatResources.getDrawable(
                    context,
                    R.drawable.background_efefef_color_fill_with_corners_10dp)
                binding?.walletOption?.setTextColor(Color.BLACK)
                binding?.myCreditsOption?.background = AppCompatResources.getDrawable(
                    context,
                    R.drawable.background_color_primary_fill_with_corners_10dp)
                binding?.myCreditsOption?.setTextColor(Color.WHITE)
            }
        }
    }

    override fun bindDataWithViews() {
        initActionBar()
        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE

        binding?.walletBalance?.text = arguments?.getString("balance")
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it, "All Transactions")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

}

