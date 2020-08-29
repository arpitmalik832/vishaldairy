package com.vishaldairy.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentMyProfileBinding
import com.vishaldairy.model.MAddress
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentMyProfile: BaseFragment<FragmentMyProfileBinding>() {

    private val SELECT_SHIPPING_ADDRESS_CODE = 101

    private var binding: FragmentMyProfileBinding? = null
    private var viewModel: VMHome? = null

    override val layoutId: Int
        get() = R.layout.fragment_my_profile

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

        binding?.addressEditBtn?.setOnClickListener {
            val fragmentShippingAddress = FragmentShippingAddress()

            val args = Bundle()
            args.putString("requestFrom", "profile")

            fragmentShippingAddress.arguments = args
            fragmentShippingAddress.setTargetFragment(
                this,
                SELECT_SHIPPING_ADDRESS_CODE
            )
            replaceFragment(
                fragmentShippingAddress,
                R.id.frame_layout,
                "11",
                true
            )
        }
    }

    override fun bindDataWithViews() {
        initActionBar()
        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE

        val user = (activity as ActivityMain?)?.user

        binding?.profileName?.text = user?.name
        user?.image?.let { binding?.profileImage?.setImageResource(it) }
        binding?.mobileNo?.text = user?.mobile
        binding?.emailId?.text = user?.email

        var address = "${user?.address?.residencyType}\n" +
                "${user?.address?.flatOrHouseNo}, " +
                "${user?.address?.apartmentOrSociety}, " +
        if(user?.address?.landmark != "" && user?.address?.landmark != null) {
            "${user.address?.landmark}, "
        } else {
            ""
        }
        address += "${user?.address?.locality}, " +
                "${user?.address?.city}, " +
                "${user?.address?.stateOrRegion} - " +
                "${user?.address?.pinCode}"
        binding?.address?.text = address

        binding?.detail?.text = user?.detail
        binding?.noOfSubscriptions?.text = user?.noOfSubscriptions.toString()
        binding?.walletBalance?.text = user?.walletBalance?.toString()
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        context?.let {
            setToolbarBackgroundColor(ContextCompat.getColor(it,R.color.colorPrimary))
        }
        binding?.title?.let {
            setToolbarTitle(it,"My Profile")
            alignTitleCenter(it)
            setToolbarTitleTextColor(it, Color.WHITE)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back_white)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) {
            if(requestCode == SELECT_SHIPPING_ADDRESS_CODE) {
                val selectedAddressJson = data?.getStringExtra("selectedAddress")
                val selectedAddress: MAddress? = Gson().fromJson(
                    selectedAddressJson,
                    MAddress::class.java
                )
                (activity as ActivityMain?)?.user?.address = selectedAddress
                var address: String = "${selectedAddress?.residencyType}\n" +
                        "${selectedAddress?.flatOrHouseNo}, " +
                        "${selectedAddress?.apartmentOrSociety}, " +
                if(selectedAddress?.landmark != "" || selectedAddress.landmark != null) {
                    "${selectedAddress?.landmark}, "
                } else {
                    ""
                }
                address += "${selectedAddress?.locality}, " +
                        "${selectedAddress?.city}, " +
                        "${selectedAddress?.stateOrRegion} - " +
                        "${selectedAddress?.pinCode}"
                binding?.address?.text = address
            }
        }
    }

}