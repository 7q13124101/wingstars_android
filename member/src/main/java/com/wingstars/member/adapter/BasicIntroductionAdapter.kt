package com.wingstars.member.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.member.databinding.ItemBasicIntroductionListBinding

data class BasicIntroductionFunBean(
    var title: String,
    var image: Int,
    var content: String
)

class BasicIntroductionAdapter(
    private var dataList: MutableList<BasicIntroductionFunBean>?
) : RecyclerView.Adapter<BasicIntroductionAdapter.NormalItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemBasicIntroductionListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

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
    fun setList(list: MutableList<BasicIntroductionFunBean>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<BasicIntroductionFunBean>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemBasicIntroductionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int) {
            var data = dataList!![position]
            binding.image.setImageResource(data.image)
            binding.title.text = data.title
            binding.content.text = data.content

        }


        fun onBind(position: Int) {
        }
    }

}