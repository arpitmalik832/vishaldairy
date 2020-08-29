package com.vishaldairy.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vishaldairy.R
import com.vishaldairy.model.MCartItem
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.ui.fragment.FragmentShoppingCart
import com.vishaldairy.utils.AppMethods
import com.vishaldairy.utils.OtherConstants
import com.vishaldairy.viewModel.VMHome

class ShoppingCartAdapter(
    var context: Context,
    var viewModel: VMHome,
    var fragment: FragmentShoppingCart,
    private var listener: OnClick<MCartItem>,
    private var data: ArrayList<MCartItem>
): RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val productName: AppCompatTextView? = itemView.findViewById(R.id.product_name)
        val productImage: AppCompatImageView? = itemView.findViewById(R.id.product_image)
        val productPrice: AppCompatTextView? = itemView.findViewById(R.id.product_price)
        val productSalePrice: AppCompatTextView? = itemView.findViewById(R.id.product_sale_price)
        val productAmount: AppCompatTextView? = itemView.findViewById(R.id.product_amount)
        val productStatus: AppCompatTextView? = itemView.findViewById(R.id.product_status)
        val quantityBtn: ConstraintLayout? = itemView.findViewById(R.id.quantity_button)
        val productQuantity: AppCompatTextView? = itemView.findViewById(R.id.product_quantity)
        val decreaseBtn: AppCompatImageView? = itemView.findViewById(R.id.decrease_quantity)
        val increaseBtn: AppCompatImageView? = itemView.findViewById(R.id.increase_quantity)
        val removeBtn: AppCompatTextView? = itemView.findViewById(R.id.remove_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_cart_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setDataList(data: ArrayList<MCartItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.productName?.text = data[position].name
        AppMethods.loadImage(
            context,
            data[position].imageSrc,
            holder.productImage,
            R.drawable.image_placeholder
        )

        data[position].on_sale?.let {
            if(it) {
                holder.productSalePrice?.text = data[position].sale_price?.let { it1 ->
                    AppMethods.formatPriceText(
                        it1
                    )
                }
            } else {
                holder.productSalePrice?.text = ""
            }
        }
        if(holder.productSalePrice?.text != "" && holder.productSalePrice?.text != null) {
            val productPriceText = SpannableString(data[position].regular_price?.let {
                AppMethods.formatPriceText(
                    it
                )
            })
            productPriceText.setSpan(
                StrikethroughSpan(),
                0,
                productPriceText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.productPrice?.text = productPriceText
        } else {
            val productPriceText = data[position].regular_price?.let { AppMethods.formatPriceText(it) }
            holder.productPrice?.text = productPriceText
        }

        holder.productAmount?.text = data[position].line_price?.let { AppMethods.formatPriceText(it) }

        holder.productStatus?.text = data[position].stock_status
        when(data[position].stock_status) {
            OtherConstants.PRODUCT_OUT_OF_STOCK -> {
                holder.productStatus?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red3
                    )
                )

                holder.quantityBtn?.isEnabled = false
                holder.productQuantity?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.grey
                    )
                )
                holder.decreaseBtn?.setImageResource(R.drawable.icon_remove_inactive)
                holder.decreaseBtn?.isEnabled = false
                holder.increaseBtn?.setImageResource(R.drawable.icon_add_inactive)
                holder.increaseBtn?.isEnabled = false
            }
            OtherConstants.PRODUCT_IN_STOCK -> {
                holder.productStatus?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )

                holder.quantityBtn?.isEnabled = true
                holder.productQuantity?.setTextColor(Color.BLACK)
                holder.decreaseBtn?.isEnabled = true
                holder.decreaseBtn?.setImageResource(R.drawable.icon_remove_color_primary)
                holder.increaseBtn?.isEnabled = true
                holder.increaseBtn?.setImageResource(R.drawable.icon_add_color_primary)
            }
            else -> {

            }
        }

        holder.productQuantity?.text = data[position].quantity.toString()

        holder.decreaseBtn?.setOnClickListener {
            var quantity = holder.productQuantity?.text.toString().toInt()
            if(quantity > 1) {
                holder.decreaseBtn.isEnabled = false
                holder.decreaseBtn.setImageResource(R.drawable.icon_remove_inactive)
                quantity--
                viewModel.updateQuantityOfItemInCart(
                    quantity,
                    fragment,
                    OtherConstants.CART_QUANTITY_BTN_FIRST_IMAGE,
                    data = data,
                    position = position,
                    holder = holder
                )
            }
        }

        holder.increaseBtn?.setOnClickListener {
            holder.increaseBtn.isEnabled = false
            holder.increaseBtn.setImageResource(R.drawable.icon_add_inactive)
            var quantity = holder.productQuantity?.text.toString().toInt()
            quantity++
            viewModel.updateQuantityOfItemInCart(
                quantity,
                fragment,
                OtherConstants.CART_QUANTITY_BTN_SECOND_IMAGE,
                data = data,
                position = position,
                holder = holder
            )
        }

        holder.removeBtn?.setOnClickListener{
            holder.removeBtn.isEnabled = false
            holder.removeBtn.background = ContextCompat.getDrawable(
                context,
                R.drawable.background_d4d4d4_color_fill_with_corners_15dp
            )
            viewModel.deleteItemFromCart(
                fragment,
                OtherConstants.CART_REMOVE_BTN,
                data = data,
                position = position,
                holder = holder
            )
        }
    }
}