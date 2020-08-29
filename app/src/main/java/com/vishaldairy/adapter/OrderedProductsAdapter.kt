package com.vishaldairy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.vishaldairy.R
import com.vishaldairy.databinding.FragmentOrderDetailBinding
import com.vishaldairy.model.MOrderedProduct
import com.vishaldairy.viewModel.VMHome

class OrderedProductsAdapter(
    var context: Context,
    var viewModel: VMHome,
    var binding: FragmentOrderDetailBinding,
    private var data: ArrayList<MOrderedProduct>
): RecyclerView.Adapter<OrderedProductsAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val name: AppCompatTextView? = itemView.findViewById(R.id.product_name)
        val image: AppCompatImageView? = itemView.findViewById(R.id.product_image)
        val quantity: AppCompatTextView? = itemView.findViewById(R.id.product_quantity)
        val price: AppCompatTextView? = itemView.findViewById(R.id.product_price)
        val amount: AppCompatTextView? = itemView.findViewById(R.id.product_total_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_ordered_product,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setDataList(data: ArrayList<MOrderedProduct>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name?.text = data[position].name
        data[position].image?.let { holder.image?.setImageResource(it) }
        holder.quantity?.text = data[position].quantity.toString()
        val productPrice = "\u20B9 ${data[position].price}"
        holder.price?.text = productPrice
        val productAmount = "\u20B9 ${data[position].totalAmount}"
        holder.amount?.text = productAmount
    }

}