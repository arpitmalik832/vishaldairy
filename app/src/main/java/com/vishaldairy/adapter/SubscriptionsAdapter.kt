package com.vishaldairy.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.vishaldairy.R
import com.vishaldairy.databinding.FragmentMySubscriptionsBinding
import com.vishaldairy.model.*
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.viewModel.VMHome
import java.util.ArrayList

class SubscriptionsAdapter(
    private val context: Context,
    var viewModel: VMHome,
    var binding: FragmentMySubscriptionsBinding,
    private var listener: OnClick<MSubscription>,
    private var data: ArrayList<MSubscription>
) : BaseExpandableListAdapter() {

    fun setDataList(data: ArrayList<MSubscription>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): MSubscriptionOptions? {
        val optionsList = data[groupPosition].optionsList
        return optionsList?.get(childPosition)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val optionsList = data[groupPosition].optionsList
        return optionsList?.size?:0
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean,
                              contentView: View?, parent: ViewGroup
    ): View? {
        var view = contentView
        val subscription = getGroup(groupPosition) as MSubscription

        if (view == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.layout_subscription_options, parent,false)
        }

        val editBtn: AppCompatTextView? = view?.findViewById(R.id.edit_btn)
        val vacationBtn: AppCompatTextView? = view?.findViewById(R.id.vacation_btn)

        if(subscription.status == "COMPLETED" || subscription.status == "CANCELLED") {
            editBtn?.isEnabled = false
            editBtn?.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_d4d4d4_color_fill_with_corners_10dp
            )
            vacationBtn?.isEnabled = false
            vacationBtn?.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_d4d4d4_color_fill_with_corners_10dp
            )
        } else if(subscription.status == "YET TO START" || subscription.status == "PROCESSING") {
            editBtn?.isEnabled = true
            editBtn?.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_color_primary_fill_with_corners_10dp
            )
            vacationBtn?.isEnabled = true
            vacationBtn?.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_color_primary_fill_with_corners_10dp
            )
        }

        editBtn?.setOnClickListener {
            listener.onClick(
                subscription,
                groupPosition,
                it
            )
        }

        vacationBtn?.setOnClickListener {
            listener.onClick(
                subscription,
                groupPosition,
                it
            )
        }

        return view
    }

    override fun getGroup(groupPosition: Int): Any {
        return data[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return data.size
    }

    override fun getGroupView(groupPosition: Int, isLastChild: Boolean, contentView: View?,
                              parent: ViewGroup
    ): View? {
        var view = contentView
        val subscription = getGroup(groupPosition) as MSubscription
        if (view == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.layout_subscription, parent, false)
        }

        val productName: AppCompatTextView? = view?.findViewById(R.id.product_name)
        val productQuantity: AppCompatTextView? = view?.findViewById(R.id.product_quantity)
        val productAmount: AppCompatTextView? = view?.findViewById(R.id.subscription_amount)
        val timeSpan: AppCompatTextView? = view?.findViewById(R.id.subscription_time_span)
        val deliveryTime: AppCompatTextView? = view?.findViewById(R.id.subscription_delivery_time)
        val status: AppCompatTextView? = view?.findViewById(R.id.subscription_status)

        productName?.text = subscription.product?.name
        productQuantity?.text = subscription.product?.quantity.toString()
        val productTotalAmount = " \u20B9 ${subscription.product?.totalAmount}"
        productAmount?.text = productTotalAmount
        val subscriptionTImeSpan = "${subscription.startDate} to ${subscription.endDate}"
        timeSpan?.text = subscriptionTImeSpan
        val subscriptionDeliveryTime = " Delivery time ${subscription.deliveryTime}"
        deliveryTime?.text = subscriptionDeliveryTime

        status?.text = subscription.status
        when(subscription.status) {
            "YET TO START" -> {
                status?.setTextColor(Color.BLACK)
            }
            "PROCESSING" -> {
                status?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }
            "COMPLETED" -> {
                status?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
            }
            "CANCELLED" -> {
                status?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red3
                    )
                )
            }
            else -> {

            }
        }

        return view
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}