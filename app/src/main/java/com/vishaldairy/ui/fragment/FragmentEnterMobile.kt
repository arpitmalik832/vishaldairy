package com.vishaldairy.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentEnterMobileBinding
import com.vishaldairy.utils.OtherConstants
import com.vishaldairy.viewModel.VMAuth

class FragmentEnterMobile: BaseFragment<FragmentEnterMobileBinding>() {

    private var binding: FragmentEnterMobileBinding? = null
    private var viewModel: VMAuth? = null

    override val layoutId: Int
        get() = R.layout.fragment_enter_mobile

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMAuth::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this
    }

    override fun initListeners() {
        setCountryCode()

        binding?.sendOtpBtn?.setOnClickListener {
            val phoneNo = binding?.phoneNumber?.text.toString().substring(6)
            if(!TextUtils.isEmpty(phoneNo)){
                binding?.pbMobile?.visibility = View.VISIBLE
                binding?.sendOtpBtn?.text = ""
                viewModel?.hitGenerateOtp(phoneNo)
            }else{
                Toast.makeText(context,"Invalid Mobile Number",Toast.LENGTH_SHORT).show()
            }
        }

        viewModel?.loginResponse?.observe(this, Observer {
            if(it==true){
                val fragmentEnterOTP = FragmentEnterOTP()
                val phoneNo = binding?.phoneNumber?.text.toString().substring(6)
                val phone = "+91$phoneNo"
                val args = Bundle()
                args.putString("mobile",phone)
                fragmentEnterOTP.arguments = args
                replaceFragment(
                    fragmentEnterOTP,
                    R.id.frame_layout,
                    "1",
                    true
                )
            }
            binding?.sendOtpBtn?.text = ("Send OTP")
            binding?.pbMobile?.visibility = View.GONE
        })
    }

    private fun setCountryCode() {
        binding?.phoneNumber?.setText(OtherConstants.COUNTRY_CODE)
        binding?.phoneNumber?.text?.length?.let {
            Selection.setSelection(binding?.phoneNumber?.text,
                it
            )
        }

        binding?.phoneNumber?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length == 16) {
                    val inputMethodManager: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    binding?.sendOtpBtn?.isEnabled = true
                    binding?.sendOtpBtn?.background = context?.let { ContextCompat.getDrawable(it, R.drawable.background_color_primary_fill_with_corners_10dp) }
                } else {
                    binding?.sendOtpBtn?.isEnabled = false
                    binding?.sendOtpBtn?.background = context?.let { ContextCompat.getDrawable(it, R.drawable.background_d4d4d4_color_fill_with_corners_10dp) }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith("+91 | ")) {
                    binding?.phoneNumber?.setText(OtherConstants.COUNTRY_CODE)
                    binding?.phoneNumber?.text?.length?.let {
                        Selection.setSelection(binding?.phoneNumber?.text,
                            it
                        )
                    }
                }
            }
        })
    }

    override fun bindDataWithViews() {
    }

}