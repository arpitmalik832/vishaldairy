package com.vishaldairy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import com.vishaldairy.R
import com.vishaldairy.databinding.FragmentFaqBinding
import com.vishaldairy.model.*
import com.vishaldairy.viewModel.VMHome
import java.util.ArrayList

class FaqAdapter(
    private val context: Context,
    var viewModel: VMHome,
    var binding: FragmentFaqBinding,
    private var data: ArrayList<MFaq>
) : BaseExpandableListAdapter() {

    fun setDataList(data: ArrayList<MFaq>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): MFaqAnswer? {
        val answersList = data[groupPosition].answersList
        return answersList?.get(childPosition)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val answersList = data[groupPosition].answersList
        return answersList?.size?:0
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean,
                              contentView: View?, parent: ViewGroup
    ): View? {
        var view = contentView
        val faq = getChild(groupPosition, childPosition) as MFaqAnswer

        if (view == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.layout_faq_answer, parent,false)
        }

        val answerText: AppCompatTextView? = view?.findViewById(R.id.answer)

        answerText?.text = faq.answer

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
        val faq = getGroup(groupPosition) as MFaq
        if (view == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.layout_faq, parent, false)
        }

        val question: AppCompatTextView? = view?.findViewById(R.id.question)

        question?.text = faq.question

        return view
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}