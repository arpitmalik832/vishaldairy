package com.vishaldairy.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishaldairy.R
import com.vishaldairy.adapter.PaymentMethodAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentAddMoneyBinding
import com.vishaldairy.utils.OtherConstants
import com.vishaldairy.viewModel.VMHome

class FragmentAddMoney: BaseFragment<FragmentAddMoneyBinding>() {

    var binding: FragmentAddMoneyBinding? = null
    private var viewModel: VMHome? = null
    private var adapterPaymentMethod: PaymentMethodAdapter? = null

    override val layoutId: Int
        get() = R.layout.fragment_add_money

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterPaymentMethod = context?.let {context ->
            viewModel?.let {viewModel ->
                PaymentMethodAdapter(
                    context,
                    viewModel,
                    this,
                    ArrayList()
                )
            }
        }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.recyclerView?.layoutManager = layoutManager
        binding?.recyclerView?.adapter = adapterPaymentMethod
        binding?.recyclerView?.setHasFixedSize(true)
    }

    override fun initListeners() {

        viewModel?.paymentMethodsList?.observe(this, Observer {
            adapterPaymentMethod?.setDataList(it)
        })

        binding?.appBarCloseIcon?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun bindDataWithViews() {
        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE

        viewModel?.fetchPaymentMethods(this)

        binding?.appBarText?.text = arguments?.getString("money")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OtherConstants.MONEY_ADDED_CODE) {
            if(resultCode == RESULT_OK) {
                activity?.onBackPressed()
            }
        }
    }
}

