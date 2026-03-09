package com.wingstars.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.wingstars.base.net.beans.FrequentlyQuestionsResponse
import com.wingstars.user.R

class FaqExpandableAdapter(
    private val context: Context,
    private val groups: List<FrequentlyQuestionsResponse.Data.GroupDto>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = groups.size

    override fun getChildrenCount(groupPosition: Int): Int = groups[groupPosition].insideData.size

    override fun getGroup(groupPosition: Int): Any = groups[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        groups[groupPosition].insideData[childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_frequently_question_list, parent, false)
        val tvTitle = view.findViewById<TextView>(R.id.tv_points_task)
        tvTitle.text = groups[groupPosition].topTitle
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val item = groups[groupPosition].insideData[childPosition]
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_question_list, parent, false)

        val tvNumber = view.findViewById<TextView>(R.id.tv_item_question_number)
        val tvTitle = view.findViewById<TextView>(R.id.tv_item_question_title)
        val ivArrow = view.findViewById<ImageView>(R.id.iv_item_question_image)
        val llContentContainer = view.findViewById<LinearLayout>(R.id.ll_questions_count)

        tvNumber.text = item.titleNum

        // Set Q1, Q2 color to theme pink
        tvNumber.setTextColor(context.getColor(R.color.color_DE9DBA))
        
        tvTitle.text = item.title

        if (item.isExpanded) {
            ivArrow.setImageResource(R.drawable.ic_arrow_up)
            llContentContainer.visibility = View.VISIBLE
            
            llContentContainer.removeAllViews()
            item.content.forEach { line ->
                val contentView = LayoutInflater.from(context)
                    .inflate(R.layout.item_questions_count, llContentContainer, false)
                
                val ivDot = contentView.findViewById<ImageView>(R.id.tv_items_count_image)
                val tvContent = contentView.findViewById<TextView>(R.id.tv_items_count_title)
                
                // Hide bullet points
                ivDot.visibility = View.GONE
                
                tvContent.text = line
                tvContent.setTextColor(context.getColor(R.color.color_4A5565))
                llContentContainer.addView(contentView)
            }
        } else {
            ivArrow.setImageResource(R.drawable.ic_arrow_down)
            llContentContainer.visibility = View.GONE
        }

        view.findViewById<View>(R.id.ll_item_question).setOnClickListener {
            item.isExpanded = !item.isExpanded
            notifyDataSetChanged()
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}