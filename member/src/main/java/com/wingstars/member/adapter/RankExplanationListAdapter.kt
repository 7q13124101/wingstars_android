package com.wingstars.member.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.member.bean.WSRankBean
import com.wingstars.member.databinding.ItemRanksExplanationListBinding


class RankExplanationListAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<WSRankBean>?
) : RecyclerView.Adapter<RankExplanationListAdapter.NormalItemViewHolder>() {
    private var pos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemRanksExplanationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<WSRankBean>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<WSRankBean>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemRanksExplanationListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            val bean = dataList!![position]
            binding.title.text = "${bean.title}"
            var htmlContent = "${bean.content}"
            binding.contents.text = Html.fromHtml(htmlContent);
        }


        fun onBind(position: Int) {

        }

        fun setMarginTip(view: View, top: Int) {
            var params = view.layoutParams as LinearLayout.LayoutParams
            params.topMargin = top
            view.layoutParams = params
        }
    }


}