package com.vishaldairy.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentEnterOtpBinding
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.viewModel.VMAuth
import kotlinx.android.synthetic.main.layout_dashboard_category.view.*

class FragmentEnterOTP: BaseFragment<FragmentEnterOtpBinding>() {

    private var binding: FragmentEnterOtpBinding? = null
    private var viewModel: VMAuth? = null

    override val layoutId: Int
        get() = R.layout.fragment_enter_otp

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
        binding?.backBtn?.setOnClickListener {
            activity?.onBackPressed()
        }

        viewModel?.loginResponse?.observe(this, Observer {
            binding?.continueBtn?.text = ("Continue")
            binding?.pbOtp?.visibility = View.GONE
        })

        binding?.continueBtn?.setOnClickListener {
            binding?.continueBtn?.text = ""
            binding?.pbOtp?.visibility = View.VISIBLE
            viewModel?.hitVerifyOtp(viewModel?.otp?.value?:"")

//            if(viewModel?.otp?.value.equals(binding?.otpView?.text?.toString())){
//                val fragmentAddAddress = FragmentAddShippingAddress()
//                val args = Bundle()
//                args.putString("requestFrom",binding?.otpView?.text?.toString())
//                fragmentAddAddress.arguments = args
//                replaceFragment(
//                    fragmentAddAddress,
//                    R.id.frame_layout,
//                    "2",
//                    false
//                )
//            }else{
//                Toast.makeText(context,"Invalid Otp entered",Toast.LENGTH_SHORT).show()
//            }
        }

        binding?.otpView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length == 6) {
                    val inputMethodManager: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                    binding?.continueBtn?.isEnabled = true
                    binding?.continueBtn?.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_color_primary_fill_with_corners_10dp) }
                } else {
                    binding?.continueBtn?.isEnabled = false
                    binding?.continueBtn?.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_d4d4d4_color_fill_with_corners_10dp) }
                }
            }
        })
    }

    override fun bindDataWithViews() {
        setOtpDescription()
    }

    private fun setOtpDescription() {
        val no  = SpannableString(" ${arguments?.getString("mobile")}")
        no.setSpan(
            ForegroundColorSpan(
                Color.BLACK
            ),
            0,
            no.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding?.otpDescription?.append(no)

        val time = SpannableString(" 00:25")
        time.setSpan(
            context?.let {context ->
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            },
            0,
            time.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding?.resendTime?.append(time)
    }

    override fun onAction(data: Any?, action: Int?) {
        if(action==201){
            replaceFragment(
                    FragmentRegister(),
                    R.id.frame_layout,
                    "121",
                    false
                )
        }else{
            startActivity(Intent(context, ActivityMain::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

}