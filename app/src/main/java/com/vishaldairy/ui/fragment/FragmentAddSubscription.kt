package com.vishaldairy.ui.fragment

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.vishaldairy.R
import com.vishaldairy.base.BaseFragment
import com.vishaldairy.databinding.FragmentAddSubscriptionBinding
import com.vishaldairy.model.MProduct
import com.vishaldairy.resource.service.ServiceSubscribeProduct
import com.vishaldairy.ui.activity.ActivityMain
import com.vishaldairy.utils.Action
import com.vishaldairy.viewModel.VMHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FragmentAddSubscription : BaseFragment<FragmentAddSubscriptionBinding>() {


    private  var mStartDate:String?=null
    private  var mEndDate:String?=null


    private var binding: FragmentAddSubscriptionBinding? = null
    private var viewModel: VMHome? = null
    private var days: ArrayList<AppCompatTextView> = ArrayList()

    override val layoutId: Int
        get() = R.layout.fragment_add_subscription

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = activity?.let {
            ViewModelProvider(it).get(VMHome::class.java)
        }
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this

        binding?.daySunday?.let {
            days.add(it)
        }
        binding?.dayMonday?.let {
            days.add(it)
        }
        binding?.dayTuesday?.let {
            days.add(it)
        }
        binding?.dayWednesday?.let {
            days.add(it)
        }
        binding?.dayThursday?.let {
            days.add(it)
        }
        binding?.dayFriday?.let {
            days.add(it)
        }
        binding?.daySaturday?.let {
            days.add(it)
        }

    }

    override fun initListeners() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(500)

            hideLoading()
            binding?.parentLayout?.visibility = View.VISIBLE

        }

        binding?.subscribeButton?.setOnClickListener {
            getSelectedDays()
        }
        var mYear: Int
        var mMonth: Int
        var mDay: Int

        binding?.startDateLayout?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                context!!,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val sdf = SimpleDateFormat("EEE, dd MMM",Locale.getDefault())
                    val sdf2 = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
                    val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    val selectedDate = sdf2.parse(date)
                    mStartDate = date
                    binding?.startDate?.text = sdf.format(selectedDate?:Date())
                }, mYear, mMonth, mDay
            )
            datePickerDialog.datePicker.minDate = c.timeInMillis-1000
            datePickerDialog.show()
        }

        binding?.rechargeAfterLayout?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                context!!,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val sdf = SimpleDateFormat("EEE, dd MMM",Locale.getDefault())
                    val sdf2 = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
                    val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    val selectedDate = sdf2.parse(date)
                    mEndDate = date
                    binding?.rechargeAfter?.text = sdf.format(selectedDate?:Date())
                }, mYear, mMonth, mDay
            )
            datePickerDialog.datePicker.minDate = c.timeInMillis+1000
            datePickerDialog.show()
        }

        val productString = "${arguments?.getString("data")}"
        val product: MProduct? = Gson().fromJson(
            productString,
            MProduct::class.java
        )

        binding?.decreaseQuantityImage?.setOnClickListener {
            binding?.decreaseQuantityImage?.isEnabled = false
            if (binding?.noOfQuantity?.text.toString() != "1") {
                var quantity = binding?.noOfQuantity?.text.toString().toInt()
                if (quantity > 1) {
                    quantity--
                    binding?.noOfQuantity?.text = quantity.toString()

                    binding?.decreaseQuantityImage?.isEnabled = true

                    (activity as ActivityMain?)?.binding?.snackBarLayout?.let { coordinatorLayout ->
                        showMessage(
                            "${product?.name} updated, You can check your Cart.",
                            "",
                            coordinatorLayout
                        )
                    }
                }
            }
            binding?.decreaseQuantityImage?.isEnabled = true
        }

        binding?.increaseQuantityImage?.setOnClickListener {
            if (binding?.increaseQuantityImage?.visibility == View.VISIBLE) {
                binding?.increaseQuantityImage?.isEnabled = false

                var quantity = binding?.noOfQuantity?.text.toString().toInt()
                quantity++

                binding?.noOfQuantity?.text = quantity.toString()

                binding?.increaseQuantityImage?.isEnabled = true

                (activity as ActivityMain?)?.binding?.snackBarLayout?.let { coordinatorLayout ->
                    showMessage(
                        "${product?.name} updated, You can check your Cart.",
                        "",
                        coordinatorLayout
                    )
                }
            }
            binding?.increaseQuantityImage?.isEnabled = true
        }

        for (i in days.indices) {
            days[i].setOnClickListener {

                if (days[i].tag == "0") {
                    context?.let { context ->
                        days[i].background = ContextCompat.getDrawable(
                            context,
                            R.drawable.background_white_fill_with_color_primary_stroke_1dp_with_corners_30dp
                        )
                        days[i].setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    }
                    days[i].tag = "1"
                } else if (days[i].tag == "1") {
                    context?.let { context ->
                        days[i].background = ContextCompat.getDrawable(
                            context,
                            R.drawable.background_color_primary_fill_with_corners_30dp
                        )
                    }
                    days[i].setTextColor(Color.WHITE)
                    days[i].tag = "0"
                }

                binding?.selectedDays?.text = ""
                var noOfSelections = 0
                for (j in days.indices) {
                    if (days[j].tag == "0") {
                        if (noOfSelections in 1..6) {
                            binding?.selectedDays?.append(", ")
                        }
                        if (j == 0) {
                            binding?.selectedDays?.append("Sun")
                            noOfSelections++
                        }
                        if (j == 1) {
                            binding?.selectedDays?.append("Mon")
                            noOfSelections++
                        }
                        if (j == 2) {
                            binding?.selectedDays?.append("Tue")
                            noOfSelections++
                        }
                        if (j == 3) {
                            binding?.selectedDays?.append("Wed")
                            noOfSelections++
                        }
                        if (j == 4) {
                            binding?.selectedDays?.append("Thu")
                            noOfSelections++
                        }
                        if (j == 5) {
                            binding?.selectedDays?.append("Fri")
                            noOfSelections++
                        }
                        if (j == 6) {
                            binding?.selectedDays?.append("Sat")
                            noOfSelections++
                        }
                    }
                }

                if (noOfSelections == 0) {
                    binding?.selectedDays?.text = getString(R.string.please_select_days)
                }

                if (noOfSelections == 7) {
                    binding?.selectedDays?.text = getString(R.string.daily)
                }

                if (noOfSelections == 0) {
                    context?.let { context ->
                        disableBottomButtons(context)
                    }
                } else {
                    context?.let { context ->
                        enableBottomButtons(context)
                    }
                }
            }
        }

    }

    override fun bindDataWithViews() {
        initActionBar()
        showLoading()
        activity?.findViewById<FrameLayout>(R.id.frame_layout)?.addView(dotProgressBar)
        binding?.parentLayout?.visibility = View.INVISIBLE

        val productString = "${arguments?.getString("data")}"
        val product: MProduct? = Gson().fromJson(
            productString,
            MProduct::class.java
        )

        binding?.productName?.text = product?.name
        //product?.image?.let {
        //  binding?.productImage?.setImageResource(it)
        //}

        //val productSalePriceText = "\u20B9 ${product?.salePrice}"
        //binding?.productSalePrice?.text = productSalePriceText

        if (binding?.productSalePrice?.text != "" && binding?.productSalePrice?.text != null) {
            val productPriceText = SpannableString("\u20B9 ${product?.price}")
            productPriceText.setSpan(
                StrikethroughSpan(),
                0,
                productPriceText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding?.productPrice?.text = productPriceText
        } else {
            val productPriceText = "\u20B9 ${product?.price}"
            binding?.productPrice?.text = productPriceText
        }

        binding?.status?.text = product?.status
        context?.let {
            when (binding?.status?.text) {
                "Out of Stock" -> {
                    binding?.status?.setTextColor(
                        ContextCompat.getColor(
                            it,
                            R.color.red3
                        )
                    )

                    disableDays(it)
                    disableQuantityPerDayBtn(it)
                    disableBottomButtons(it)
                }
                "In Stock" -> {
                    binding?.status?.setTextColor(
                        ContextCompat.getColor(
                            it,
                            R.color.green
                        )
                    )

                    enableDays(it)
                    enableQuantityPerDayBtn()
                    enableBottomButtons(it)
                }
                else -> {

                }
            }

            if (binding?.selectedDays?.text == getString(R.string.please_select_days)) {
                context?.let { context ->
                    disableBottomButtons(context)
                }
            } else {
                context?.let { context ->
                    enableBottomButtons(context)
                }
            }

        }
    }

    private fun disableQuantityPerDayBtn(context: Context) {
        binding?.quantityPerDayButton?.isEnabled = false
        binding?.noOfQuantity?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.grey
            )
        )
        binding?.decreaseQuantityImage?.isEnabled = false
        binding?.decreaseQuantityImage?.setImageResource(R.drawable.icon_remove_inactive)
        binding?.increaseQuantityImage?.isEnabled = false
        binding?.increaseQuantityImage?.setImageResource(R.drawable.icon_add_inactive)
    }

    private fun enableQuantityPerDayBtn() {
        binding?.quantityPerDayButton?.isEnabled = true
        binding?.noOfQuantity?.setTextColor(Color.BLACK)
        binding?.decreaseQuantityImage?.isEnabled = true
        binding?.decreaseQuantityImage?.setImageResource(R.drawable.icon_remove_color_primary)
        binding?.increaseQuantityImage?.isEnabled = true
        binding?.increaseQuantityImage?.setImageResource(R.drawable.icon_add_color_primary)
    }

    private fun disableDays(context: Context) {
        for (i in days.indices) {
            days[i].isEnabled = false
            days[i].background = ContextCompat.getDrawable(
                context,
                R.drawable.background_white_fill_with_d4d4d4_stroke_1dp_with_corners_30dp
            )
            days[i].setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.grey
                )
            )
        }
        binding?.selectedDays?.isEnabled = false
        binding?.selectedDays?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.grey
            )
        )
    }

    private fun enableDays(context: Context) {
        for (i in days.indices) {
            days[i].isEnabled = true
            days[i].background = ContextCompat.getDrawable(
                context,
                R.drawable.background_white_fill_with_color_primary_stroke_1dp_with_corners_30dp
            )
            days[i].setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
        }
        binding?.selectedDays?.isEnabled = true
        binding?.selectedDays?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.colorPrimary
            )
        )
    }

    private fun enableBottomButtons(context: Context) {
        binding?.subscribeButton?.isEnabled = true
        binding?.subscribeButton?.background = ContextCompat.getDrawable(
            context,
            R.drawable.background_color_primary_fill_with_corners_10dp
        )
        binding?.deliverOnceButton?.isEnabled = true
        binding?.deliverOnceButton?.background = ContextCompat.getDrawable(
            context,
            R.drawable.background_white_fill_with_color_primary_stroke_2dp_with_corners_10dp
        )
        binding?.deliverOnceButton?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.colorPrimary
            )
        )
    }

    private fun disableBottomButtons(context: Context) {
        binding?.subscribeButton?.isEnabled = false
        binding?.subscribeButton?.background = ContextCompat.getDrawable(
            context,
            R.drawable.background_d4d4d4_color_fill_with_corners_10dp
        )
        binding?.deliverOnceButton?.isEnabled = false
        binding?.deliverOnceButton?.background = ContextCompat.getDrawable(
            context,
            R.drawable.background_white_fill_with_d4d4d4_stroke_2dp_with_corners_10dp
        )
        binding?.deliverOnceButton?.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.grey
            )
        )
    }

    private fun initActionBar() {
        binding?.toolbar?.let {
            setToolbar(it)
        }
        setToolbarBackgroundColor(Color.WHITE)
        binding?.title?.let {
            setToolbarTitle(it, "Add Subscription")
            setToolbarTitleTextColor(it, Color.BLACK)
        }
        enableToolbarHomeButton()
        setToolbarHomeButtonIcon(R.drawable.icon_back)
    }

    override fun showMessage(msg: String?, action: Int?) {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
    }

    fun getSelectedDays() {
        if(TextUtils.isEmpty(mStartDate) || TextUtils.isEmpty(mEndDate)){
            showMessage("Please select start Date and Recharge date",0)
            return
        }
        val selectedDays = binding?.selectedDays?.text
        val request = HashMap<String, Any>()
        if (selectedDays?.contains("daily") == true) {
            request["mon"] = true
            request["tue"] = true
            request["wed"] = true
            request["thu"] = true
            request["fri"] = true
            request["sat"] = true
            request["sun"] = true
        } else {
            request["mon"] = selectedDays?.contains("mon", true) == true
            request["tue"] = selectedDays?.contains("tue", true) == true
            request["wed"] = selectedDays?.contains("wed", true) == true
            request["thu"] = selectedDays?.contains("thu", true) == true
            request["fri"] = selectedDays?.contains("fri", true) == true
            request["sat"] = selectedDays?.contains("sat", true) == true
            request["sun"] = selectedDays?.contains("sun", true) == true
        }

            val productString = "${arguments?.getString("data")}"
            val product: MProduct? = Gson().fromJson(
                productString,
                MProduct::class.java
            )
            request["start_date"] = mStartDate?:""
            request["recharge_date"] = mEndDate?:""
            request["product_id"] = product?.id?:"0"
            request["user_id"] = Action.getUserId()
            viewModel?.subscribeProduct(request)

    }


    override fun onAction(data: Any?, action: Int?) {
        activity?.onBackPressed()
    }
}