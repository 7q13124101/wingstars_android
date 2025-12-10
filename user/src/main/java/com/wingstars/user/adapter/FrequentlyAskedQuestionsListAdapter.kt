package com.wingstars.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.wingstars.user.R
import com.wingstars.user.net.beans.FrequentlyQuestionsResponse

class FrequentlyAskedQuestionsListAdapter(
    private val context: Context
) :
    BaseExpandableListAdapter() {
    private var mInflater: LayoutInflater? = null
    private var mGroupList: List<FrequentlyQuestionsResponse.Data.GroupDto>? = null

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    fun setGroupList(groupList: List<FrequentlyQuestionsResponse.Data.GroupDto>?) {
        this.mGroupList = groupList
    }

    fun getGroupList(): List<FrequentlyQuestionsResponse.Data.GroupDto> {
        return if (mGroupList == null) ArrayList() else mGroupList!!
    }


    // 获取父项（分类）数量
    override fun getGroupCount(): Int {
        return mGroupList?.size ?: 0
    }

    // 获取每个父项下的子项数量
    override fun getChildrenCount(groupPosition: Int): Int {
        return mGroupList!![groupPosition].insideData.size
    }

    // 获取父项数据（类别）
    override fun getGroup(groupPosition: Int): Any {
        return mGroupList!![groupPosition]
    }

    // 获取子项数据（每个分类下的具体项）
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return mGroupList!![groupPosition].insideData[childPosition]
    }

    // 获取父项ID
    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    // 获取子项ID
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    // 检查某项是否稳定（通常返回false即可）
    override fun hasStableIds(): Boolean {
        return false
    }

    // 创建父项视图
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val groupView: View
        val viewHolder: GroupViewHolder
        if (convertView == null) {
            groupView = LayoutInflater.from(context)
                .inflate(R.layout.item_frequently_question_list, parent, false)
            viewHolder = GroupViewHolder(
                groupView.findViewById(R.id.tv_points_task)
            )
            groupView.tag = viewHolder
        } else {
            groupView = convertView
            viewHolder = groupView.tag as GroupViewHolder
        }
        val category = getGroup(groupPosition) as FrequentlyQuestionsResponse.Data.GroupDto
        viewHolder.tv_points_task.text = category.topTitle
        return groupView
    }

    // 创建子项视图
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childView: View
        val viewHolder: ChildViewHolder
        if (convertView == null) {
            childView =
                LayoutInflater.from(context).inflate(R.layout.item_question_list, parent, false)
            viewHolder = ChildViewHolder(
                childView.findViewById(R.id.ll_item_question),
                childView.findViewById(R.id.tv_item_question_number),
                childView.findViewById(R.id.tv_item_question_title),
                childView.findViewById(R.id.iv_item_question_image),
                childView.findViewById(R.id.ll_questions_count)
            )
            childView.tag = viewHolder
        } else {
            childView = convertView
            viewHolder = childView.tag as ChildViewHolder
        }

        val item = getChild(
            groupPosition,
            childPosition
        ) as FrequentlyQuestionsResponse.Data.GroupDto.ItemDto
        viewHolder.tv_item_question_number.text = item.titleNum
        viewHolder.tv_item_question_title.text = item.title

        viewHolder.ll_questions_count.removeAllViews()
        if (item.content.size == 1) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.item_questions_count, null)
            val tv_items_count_image = view.findViewById<ImageView>(R.id.tv_items_count_image)
            tv_items_count_image.visibility = View.GONE
            val tv_items_count_title = view.findViewById<TextView>(R.id.tv_items_count_title)
            tv_items_count_title.text = item.content[0]
            viewHolder.ll_questions_count.addView(view)
        } else if (item.content.size > 1) {
            for (i in item.content) {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.item_questions_count, null)
                val tv_items_count_image = view.findViewById<ImageView>(R.id.tv_items_count_image)
                tv_items_count_image.visibility = View.VISIBLE
                val tv_items_count_title = view.findViewById<TextView>(R.id.tv_items_count_title)
                tv_items_count_title.text = i
                viewHolder.ll_questions_count.addView(view)
            }
        }

        if (item.isExpanded) {
            viewHolder.iv_item_question_image.setImageResource(R.drawable.ic_chevron_up)
            viewHolder.ll_questions_count.visibility = View.VISIBLE
        } else {
            viewHolder.iv_item_question_image.setImageResource(R.drawable.ic_chevron_down)
            viewHolder.ll_questions_count.visibility = View.GONE
        }

        viewHolder.ll_item_question.setOnClickListener {
            item.isExpanded = !item.isExpanded
            notifyDataSetChanged()
        }
        return childView
    }

    // 检查子项是否可选
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    // 内部类：父项视图的 ViewHolder
    private class GroupViewHolder(
        val tv_points_task: TextView
    )

    // 内部类：子项视图的 ViewHolder
    private class ChildViewHolder(
        val ll_item_question: LinearLayout,
        val tv_item_question_number: TextView,
        val tv_item_question_title: TextView,
        val iv_item_question_image: ImageView,
        val ll_questions_count: LinearLayout
    )

}