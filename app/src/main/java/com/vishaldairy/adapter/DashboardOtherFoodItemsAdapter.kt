package com.vishaldairy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.vishaldairy.R
import com.vishaldairy.databinding.FragmentDashboardBinding
import com.vishaldairy.model.MCategory
import com.vishaldairy.navigator.OnClick
import com.vishaldairy.viewModel.VMHome

class DashboardOtherFoodItemsAdapter(
    var context: Context,
    var viewModel: VMHome,
    var binding: FragmentDashboardBinding,
    private var listener: OnClick<MCategory>,
    private var data: ArrayList<MCategory>
): RecyclerView.Adapter<DashboardOtherFoodItemsAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val text: AppCompatTextView? = itemView.findViewById(R.id.text)
        val image: AppCompatImageView? = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_dashboard_other_food_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setDataList(data: ArrayList<MCategory>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text?.text = data[position].name
        //data[position].image?.let { holder.image?.setImageResource(it) }

        holder.image?.setOnClickListener {
            listener.onClick(
                data[position],
                position,
                it
            )
        }
    }

}