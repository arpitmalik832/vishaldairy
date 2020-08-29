package com.vishaldairy.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.payumoney.core.PayUmoneyConfig
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentMyWalletBinding
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.utils.Action
import com.vishaldairy.utils.PayU
import com.vishaldairy.viewModel.VMHome

class FragmentMyWallet : BaseFragment<FragmentMyWalletBinding>() {

    private val productName: String = "Vishal Diary Recharge"
    private val txnId:String? by lazy { Action.generateTxn() }
    var binding: FragmentMyWalletBinding? = null
    private var viewModel: VMHome? = null

    override val layoutId: Int
        get() = R.layout.fragment_my_wallet

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
        addMoneyOptions()

        val addMoneyDefault = "\u20B9 "
        binding?.addMoney?.setText(addMoneyDefault)
        binding?.addMoney?.text?.length?.let { Selection.setSelection(binding?.addMoney?.text, it) }

        binding?.addMoney?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { sequence ->
                    if (sequence.length >= 3) {
                        when (sequence.substring(2)) {
                            "500" -> {
                                setUxForBtn(
                                    binding?.option500,
                                    binding?.option1000,
                                    binding?.option2000
                                )
                            }
                            "1000" -> {
                                setUxForBtn(
                                    binding?.option1000,
                                    binding?.option2000,
                                    binding?.option500
                                )
                            }
                            "2000" -> {
                                setUxForBtn(
                                    binding?.option2000,
                                    binding?.option500,
                                    binding?.option1000
                                )
                            }
                            else -> {
                                context?.let { context ->
                                    binding?.option500?.background = AppCompatResources.getDrawable(
                                        context,
                                        R.drawable.background_efefef_color_fill_with_corners_10dp
                                    )
                                    binding?.option500?.setTextColor(Color.BLACK)
                                    binding?.option1000?.background =
                                        AppCompatResources.getDrawable(
                                            context,
                                            R.drawable.background_efefef_color_fill_with_corners_10dp
                                        )
                                    binding?.option1000?.setTextColor(Color.BLACK)
                                    binding?.option2000?.background =
                                        AppCompatResources.getDrawable(
                                            context,
                                            R.drawable.background_efefef_color_fill_with_corners_10dp
                                        )
                                    binding?.option2000?.setTextColor(Color.BLACK)
                                }
                            }
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith(addMoneyDefault)) {
                    binding?.addMoney?.setText(addMoneyDefault)
                    binding?.addMoney?.text?.length?.let {
                        Selection.setSelection(
                            binding?.addMoney?.text,
                            it
                        )
                    }
                }
            }
        })

        binding?.viewAllTransactions?.setOnClickListener {
            val fragmentAllTransactions = FragmentAllTransactions()

            val args = Bundle()
            args.putString("balance", binding?.walletBalance?.text.toString())

            fragmentAllTransactions.arguments = args
            replaceFragment(
                fragmentAllTransactions,
                R.id.frame_layout,
                "7",
                true
            )
        }


        binding?.addMoneyButton?.setOnClickListener {
            if (binding?.addMoney?.text?.length == 2 || binding?.addMoney?.text.toString() == "\u20B9 0") {
                (activity as ActivityMain?)?.binding?.snackBarLayout?.let {
                    showMessage(
                        "Please Enter Money to add",
                        "",
                        it
                    )
                }
            } else {
//                val fragmentAddMoney = FragmentAddMoney()
//                val args = Bundle()
//                args.putString("money", binding?.addMoney?.text.toString())
//
                if (!TextUtils.isEmpty(binding?.addMoney?.text)) {
                    initPaymentGateway("1")
                }
//                fragmentAddMoney.arguments = args
//                replaceFragment(
//                    fragmentAddMoney,
//                    R.id.frame_layout,
//                    "8",
//                    true
//                )
            }
        }
    }

    private fun initPaymentGateway(value: String) {

        val builder: PayUmoneySdkInitializer.PaymentParam.Builder =
            PayUmoneySdkInitializer.PaymentParam.Builder()

        val payUmoneyConfig = PayUmoneyConfig.getInstance()

        //Use this to set your custom text on result screen button

        //Use this to set your custom text on result screen button
        payUmoneyConfig.doneButtonText = "Done"

        //Use this to set your custom title for the activity

        //Use this to set your custom title for the activity
        payUmoneyConfig.payUmoneyActivityTitle = "Vishal Dairy"

        builder.setAmount(value)                          // Payment amount
            .setTxnId(txnId)
            .setPhone(Action.getPhoneNumber())                                           // User Phone number
            .setProductName(productName)                   // Product Name or description
            .setFirstName(Action.getUserName())                              // User First name
            .setEmail(Action.getEmailAddress())                                            // User Email ID
            .setsUrl(PayU.successUrl)                    // Success URL (surl)
            .setfUrl(PayU.failedUrl)                     //Failure URL (furl)
            .setUdf1("")
            .setUdf2("")
            .setUdf3("")
            .setUdf4("")
            .setUdf5("")
            .setUdf6("")
            .setUdf7("")
            .setUdf8("")
            .setUdf9("")
            .setUdf10("")
            .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
            .setKey(PayU.merchantKey)                        // Merchant key
            .setMerchantId(PayU.merchantId)
        try {
            val hashKey = generateHashKey(value)
            val paymentParam = builder.build()
            paymentParam.setMerchantHash(hashKey)
            PayUmoneyFlowManager.startPayUMoneyFlow(
                paymentParam,
                activity,
                R.style.AppTheme_NoActionBar,
                false)
        }catch (e:Exception){
            Toast.makeText(context,"Failed with ${e.localizedMessage}",Toast.LENGTH_SHORT).show()
        }

    }

    private fun generateHashKey(amount:String): String? {
        val hashSequence  =  "${PayU.merchantKey}|${txnId}|${amount}|${productName}|${Action.getUserName()}|${Action.getEmailAddress()}|||||||||||${PayU.salt}"
        return PayU.hashCal("SHA-512", hashSequence)
    }

    private fun addMoneyOptions() {
        binding?.option500?.setOnClickListener {
            setUxForBtn(
                binding?.option500,
                binding?.option1000,
                binding?.option2000
            )
            binding?.addMoney?.setText(binding?.option500?.text)
        }

        binding?.option1000?.setOnClickListener {
            setUxForBtn(
                binding?.option1000,
                binding?.option2000,
                binding?.option500
            )
            binding?.addMoney?.setText(binding?.option1000?.text)
        }

        binding?.option2000?.setOnClickListener {
            setUxForBtn(
                binding?.option2000,
                binding?.option500,
                binding?.option1000
            )
            binding?.addMoney?.setText(binding?.option2000?.text)
        }
    }

    private fun setUxForBtn(
        btn1: AppCompatTextView?,
        btn2: AppCompatTextView?,
        btn3: AppCompatTextView?
    ) {
        context?.let { context ->
            btn1?.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_color_primary_fill_with_corners_10dp
            )
            btn1?.setTextColor(Color.WHITE)
            btn2?.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_efefef_color_fill_with_corners_10dp
            )
            btn2?.setTextColor(Color.BLACK)
            btn3?.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_efefef_color_fill_with_corners_10dp
            )
            btn3?.setTextColor(Color.BLACK)
        }
    }

    override fun bindDataWithViews() {
        initActionBar()
        viewModel?.fetchBalance(
            this
        )
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it, "My Wallet")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

}

