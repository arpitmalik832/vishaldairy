package com.vishaldairy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vishaldairy.R
import com.vishaldairy.databinding.FragmentShippingAddressBinding
import com.vishaldairy.model.MAddress
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.viewModel.VMHome

class ShippingAddressAdapter(
    var context: Context,
    var viewModel: VMHome,
    var binding: FragmentShippingAddressBinding,
    private var listener: OnClick<MAddress>,
    private var data: ArrayList<MAddress>
): RecyclerView.Adapter<ShippingAddressAdapter.ViewHolder>() {

    private var noOfChecked = 0

    class ViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val userName: AppCompatTextView? = itemView.findViewById(R.id.user_name)
        val residencyType: AppCompatTextView? = itemView.findViewById(R.id.residency_type)
        val addressLine1: AppCompatTextView? = itemView.findViewById(R.id.address_line_1)
        val addressLine2: AppCompatTextView? = itemView.findViewById(R.id.address_line_2)
        val addressLine3: AppCompatTextView? = itemView.findViewById(R.id.address_line_3)
        val editBtn: AppCompatTextView? = itemView.findViewById(R.id.edit_button)
        val checkBox: AppCompatCheckBox? = itemView.findViewById(R.id.check_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_shipping_address,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setDataList(data: ArrayList<MAddress>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName?.text = data[position].name
        holder.residencyType?.text = data[position].residencyType
        val addressLine1 = "${data[position].flatOrHouseNo}, ${data[position].apartmentOrSociety}"
        holder.addressLine1?.text = addressLine1
        var addressLine2 = if(data[position].landmark != "" && data[position].landmark != null) {
            "${data[position].landmark}, "
        } else {
            ""
        }
        addressLine2 += "${data[position].locality}"
        holder.addressLine2?.text = addressLine2
        val addressLine3 = "${data[position].city}, ${data[position].stateOrRegion} - ${data[position].pinCode}"
        holder.addressLine3?.text = addressLine3

        holder.checkBox?.setOnCheckedChangeListener { _, bool ->
            if(bool) {
                if(noOfChecked == 0) {
                    noOfChecked++
                } else {
                    holder.checkBox.isChecked = false
                }
            } else {
                noOfChecked--
            }

            when(noOfChecked) {
                1 -> {
                    listener.onClick(
                        data[position],
                        position,
                        holder.checkBox
                    )
                    binding.button.isEnabled = true
                    binding.button.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.background_color_primary_fill_with_corners_10dp
                    )
                }
                0 -> {
                    binding.button.isEnabled = false
                    binding.button.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.background_d4d4d4_color_fill_with_corners_10dp
                    )
                }
            }
        }
    }

}