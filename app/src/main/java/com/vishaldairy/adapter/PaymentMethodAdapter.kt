package com.vishaldairy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.vishaldairy.R
import com.vishaldairy.model.MPaymentMethod
import com.vishaldairy.ui.fragment.FragmentAddMoney
import com.vishaldairy.viewModel.VMHome

class PaymentMethodAdapter(
    var context: Context,
    var viewModel: VMHome,
    var fragment: FragmentAddMoney,
    private var data: ArrayList<MPaymentMethod>
): RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView)  {

        val view1: LinearLayout = itemView.findViewById(R.id.layout_1)
        val name1: AppCompatTextView = itemView.findViewById(R.id.title_1)
        val description1: AppCompatTextView = itemView.findViewById(R.id.description_1)
        val image1: AppCompatImageView = itemView.findViewById(R.id.icon_1)

        val view2: LinearLayout = itemView.findViewById(R.id.layout_2)
        val name2: AppCompatTextView = itemView.findViewById(R.id.title_2)
        val description2: AppCompatTextView = itemView.findViewById(R.id.description_2)
        val image2: AppCompatImageView = itemView.findViewById(R.id.icon_2)

        val view3: LinearLayout = itemView.findViewById(R.id.layout_3)
        val name3: AppCompatTextView = itemView.findViewById(R.id.title_3)
        val description3: AppCompatTextView = itemView.findViewById(R.id.description_3)
        val image3: AppCompatImageView = itemView.findViewById(R.id.icon_3)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_payment_method,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setDataList(data: ArrayList<MPaymentMethod>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (position) {

            0 -> {
                holder.view1.visibility = View.VISIBLE

                holder.name1.text = data[position].title
                holder.description1.text = data[position].description
                data[position].image?.let { holder.image1.setImageResource(it) }

                holder.view1.setOnClickListener {
                    viewModel.addBalance(fragment)
                }
            }

            data.lastIndex -> {
                holder.view3.visibility = View.VISIBLE

                holder.name3.text = data[position].title
                holder.description3.text = data[position].description
                data[position].image?.let { holder.image3.setImageResource(it) }

                holder.view3.setOnClickListener {
                    viewModel.addBalance(fragment)
                }
            }

            else -> {
                holder.view2.visibility = View.VISIBLE

                holder.name2.text = data[position].title
                holder.description2.text = data[position].description
                data[position].image?.let { holder.image2.setImageResource(it) }

                holder.view2.setOnClickListener {
                    viewModel.addBalance(fragment)
                }
            }
        }

    }

}