package com.vishaldairy.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vishaldairy.R
import com.vishaldairy.databinding.FragmentMyOrdersBinding
import com.vishaldairy.model.MOrder
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.viewModel.VMHome

class OrdersAdapter(
    var context: Context,
    var viewModel: VMHome,
    var binding: FragmentMyOrdersBinding,
    private var listener: OnClick<MOrder>,
    private var data: ArrayList<MOrder>
): RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        var orderNo: AppCompatTextView? = itemView.findViewById(R.id.order_no)
        var orderDate: AppCompatTextView? = itemView.findViewById(R.id.order_date)
        var noOfProducts: AppCompatTextView? = itemView.findViewById(R.id.products)
        var totalAmount: AppCompatTextView? = itemView.findViewById(R.id.amount)
        var status: AppCompatTextView? = itemView.findViewById(R.id.status)
        var detailsBtn: AppCompatTextView? = itemView.findViewById(R.id.details_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_order,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setDataList(data: ArrayList<MOrder>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderNo?.text = data[position].no.toString()
        holder.orderDate?.text = data[position].date
        holder.noOfProducts?.text = data[position].noOfProducts.toString()
        val totalAmount = "\u20B9 ${data[position].totalAmount}"
        holder.totalAmount?.text = totalAmount

        holder.status?.text = data[position].status
        when(data[position].status) {
            "PROCESSING" -> {
                holder.status?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }
            "DELIVERED" -> {
                holder.status?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
            }
            "CANCELLED" -> {
                holder.status?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red3
                    )
                )
            }
        }

        holder.detailsBtn?.setOnClickListener {
            listener.onClick(
                data[position],
                position,
                it
            )
        }
    }

}