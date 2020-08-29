package com.vishaldairy.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentAddShippingAddressBinding
import com.vishaldairy.model.MAddress
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentAddShippingAddress: BaseFragment<FragmentAddShippingAddressBinding>() {

    private var binding: FragmentAddShippingAddressBinding? = null
    private var viewModel: VMHome? = null

    override val layoutId: Int
        get() = R.layout.fragment_add_shipping_address

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

        binding?.residencyApartment?.setOnClickListener {
            context?.let {context ->
                binding?.residencyIndividual?.tag = "0"
                binding?.residencyIndividual?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                binding?.residencyIndividual?.background = ActivityCompat.getDrawable(
                    context,
                    R.drawable.background_e0f4ff_color_fill_with_corners_10dp
                )
                binding?.residencyApartment?.tag = "1"
                binding?.residencyApartment?.setTextColor(
                    Color.WHITE
                )
                binding?.residencyApartment?.background = ActivityCompat.getDrawable(
                    context,
                    R.drawable.background_color_primary_fill_with_corners_10dp
                )
            }
        }

        binding?.residencyIndividual?.setOnClickListener {
            context?.let {context ->
                binding?.residencyApartment?.tag = "0"
                binding?.residencyApartment?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                binding?.residencyApartment?.background = ActivityCompat.getDrawable(
                    context,
                    R.drawable.background_e0f4ff_color_fill_with_corners_10dp
                )
                binding?.residencyIndividual?.tag = "1"
                binding?.residencyIndividual?.setTextColor(
                    Color.WHITE
                )
                binding?.residencyIndividual?.background = ActivityCompat.getDrawable(
                    context,
                    R.drawable.background_color_primary_fill_with_corners_10dp
                )
            }
        }

        binding?.addAddressBtn?.setOnClickListener {
            var isActive = true
            binding?.fullName?.let { fullName ->
                isActive = checkEditTextIsNotEmpty(fullName, "Please Fill Name")
            }
            binding?.flatHouseNo?.let {flatHouseNo ->
                isActive = checkEditTextIsNotEmpty(flatHouseNo,"Please Fill Flat/House No")
            }
            binding?.apartmentSociety?.let {apartmentSociety ->
                isActive = checkEditTextIsNotEmpty(apartmentSociety, "Please Fill Apartment/Society")
            }
            binding?.locality?.let {locality ->
                isActive = checkEditTextIsNotEmpty(locality, "Please Fill Locality")
            }
            binding?.city?.let {city ->
                isActive = checkEditTextIsNotEmpty(city, "Please Fill City")
            }
            binding?.stateRegion?.let {stateRegion ->
                isActive = checkEditTextIsNotEmpty(stateRegion, "Please Fill State/Region")
            }
            binding?.pinCode?.text?.isEmpty()?.let {isEmpty ->
                binding?.pinCode?.text?.equals("")?.let {equals ->
                    if (isEmpty || binding?.pinCode?.length() == 0 || equals || binding?.pinCode?.text == null || binding?.pinCode?.length() != 6) {
                        binding?.pinCode?.error = "Please Fill Correct Pin Code"
                        isActive = false
                    }
                }
            }

            if(isActive) {

                var residency = ""
                if (binding?.residencyIndividual?.tag == "1") {
                    residency = binding?.residencyIndividual?.text.toString()
                } else if (binding?.residencyApartment?.tag == "1") {
                   residency = binding?.residencyApartment?.text.toString()
                }

                val newAddress = MAddress(
                    binding?.fullName?.text.toString(),
                    residency,
                    binding?.flatHouseNo?.text.toString(),
                    binding?.apartmentSociety?.text.toString(),
                    binding?.landmark?.text.toString(),
                    binding?.locality?.text.toString(),
                    binding?.city?.text.toString(),
                    binding?.stateRegion?.text.toString(),
                    binding?.pinCode?.text.toString().toInt()
                )

                val intent = Intent(context, FragmentAddShippingAddress::class.java)
                val addressJson = Gson().toJson(newAddress)
                intent.putExtra("newAddress", addressJson)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                activity?.onBackPressed()
            }
        }
    }

    private fun checkEditTextIsNotEmpty(editText: AppCompatEditText, errorString: String): Boolean {
        editText.text?.isEmpty()?.let {isEmpty ->
            editText.text?.equals("")?.let {equals ->
                if (isEmpty || editText.text?.length == 0 || equals || editText.text == null) {
                    editText.error = errorString
                    return false
                }
            }
        }
        return true
    }

    override fun bindDataWithViews() {
        initActionBar()
        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it,"Add New Address")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

}