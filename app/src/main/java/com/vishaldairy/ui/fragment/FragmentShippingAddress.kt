package com.vishaldairy.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.adapter.ShippingAddressAdapter
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentShippingAddressBinding
import com.vishaldairy.model.MAddress
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentShippingAddress: BaseFragment<FragmentShippingAddressBinding>(), OnClick<MAddress> {

    private val ADD_SHIPPING_ADDRESS_CODE = 100

    private var binding: FragmentShippingAddressBinding? = null
    private var viewModel: VMHome? = null
    private var adapterShippingAddress: ShippingAddressAdapter? = null
    private var selectedAddress: MAddress? = null

    override val layoutId: Int
        get() = R.layout.fragment_shipping_address

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        adapterShippingAddress = context?.let {context ->
            viewModel?.let {viewModel ->
                binding?.let {binding ->
                    ShippingAddressAdapter(
                        context,
                        viewModel,
                        binding,
                        this,
                        ArrayList()
                    )
                }
            }
        }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.recyclerView?.layoutManager = layoutManager
        binding?.recyclerView?.adapter = adapterShippingAddress
        binding?.recyclerView?.setHasFixedSize(true)
    }

    override fun initListeners() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(500)

            viewModel?.addressesList?.let {
                adapterShippingAddress?.setDataList(it)
            }

            hideLoading()
            binding?.parentLayout?.visibility = View.VISIBLE

        }

        binding?.addAddressButton?.setOnClickListener {
            val fragmentAddShippingAddress = FragmentAddShippingAddress()

            fragmentAddShippingAddress.setTargetFragment(
                this,
                ADD_SHIPPING_ADDRESS_CODE
            )
            replaceFragment(
                fragmentAddShippingAddress,
                R.id.frame_layout,
                "12",
                true
            )
        }

        binding?.button?.setOnClickListener {
            val requestFrom: String? = arguments?.getString("requestFrom","")

            if(requestFrom == "profile") {
                val intent = Intent(context, FragmentShippingAddress::class.java)
                val addressJson = Gson().toJson(selectedAddress)
                intent.putExtra("selectedAddress", addressJson)
                targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, intent)
                activity?.onBackPressed()
            } else if(requestFrom == "cart"){
                (activity as ActivityMain?)?.binding?.snackBarLayout?.let {snackBarLayout ->
                    showMessage(
                        "Payment not yet Updated",
                        "",
                        snackBarLayout
                    )
                }
            }
        }
    }

    override fun bindDataWithViews() {
        val requestFrom: String? = arguments?.getString("requestFrom","")

        if(requestFrom == "profile") {
            val title = "Select Address"
            initActionBar(title)
            binding?.button?.text = title
        } else if(requestFrom == "cart") {
            val title = "Select Shipping Address"
            val text = "Proceed to Pay"
            initActionBar(title)
            binding?.button?.text = text
        }

        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE

        viewModel?.getAddresses()

        context?.let {context ->
            binding?.button?.isEnabled = false
            binding?.button?.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_d4d4d4_color_fill_with_corners_10dp
            )
        }
    }

    private fun initActionBar(title: String) {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it,title)
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

    override fun onClick(bean: MAddress, position: Int, view: View) {
        if(view.id == R.id.check_box) {
            selectedAddress = bean
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ADD_SHIPPING_ADDRESS_CODE) {
            if(resultCode == RESULT_OK) {
                val newAddressJson = data?.getStringExtra("newAddress")
                val newAddress: MAddress? = Gson().fromJson(
                    newAddressJson,
                    MAddress::class.java
                )
                newAddress?.let {address ->
                    (activity as ActivityMain?)?.binding?.snackBarLayout?.let {snackBarLayout ->
                        showMessage(
                            "New Address $address",
                            "",
                            snackBarLayout
                        )
                    }
                }
            }
        }
    }

}