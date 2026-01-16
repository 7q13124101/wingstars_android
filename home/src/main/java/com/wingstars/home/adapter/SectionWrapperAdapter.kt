package com.wingstars.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.base.utils.DPUtils
import com.wingstars.home.R
import kotlin.text.toFloat

// home/src/main/java/com/wingstars/home/adapter/SectionWrapperAdapter.kt
class SectionWrapperAdapter(
    private val title: String,
    val innerAdapter: RecyclerView.Adapter<*>,
    private val isGrid: Boolean = false,
    private val onMoreClick: (() -> Unit)? = null,
    private val showIndicator: Boolean = false,
    private val contentPadding: SectionPadding = SectionPadding(),
    private val orientation: Int = RecyclerView.HORIZONTAL
) : RecyclerView.Adapter<SectionWrapperAdapter.ViewHolder>() {
    private var isVisible: Boolean = true

    fun setVisible(visible: Boolean) {
        if (isVisible == visible) return
        isVisible = visible

        // Vì SectionWrapperAdapter chỉ có tối đa 1 item:
        if (isVisible) notifyItemInserted(0) else notifyItemRemoved(0)
    }


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvSectionTitle)
        val btnMore: View = view.findViewById(R.id.ivSectionMore)
        val rvContent: RecyclerView = view.findViewById(R.id.rv_section_content)
        val rvIndicator: RecyclerView = view.findViewById(R.id.rv_section_indicator)

        var dotAdapter: DotIndicatorAdapter? = null
        var dataObserver: RecyclerView.AdapterDataObserver? = null
        var snapHelper: androidx.recyclerview.widget.PagerSnapHelper? = null
        var scrollListener: RecyclerView.OnScrollListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_section_container, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val start = DPUtils.dpToPx(contentPadding.startDp.toFloat(), holder.itemView.context).toInt()
        val top = DPUtils.dpToPx(contentPadding.topDp.toFloat(), holder.itemView.context).toInt()
        val end = DPUtils.dpToPx(contentPadding.endDp.toFloat(), holder.itemView.context).toInt()
        val bottom = DPUtils.dpToPx(contentPadding.bottomDp.toFloat(), holder.itemView.context).toInt()

        holder.rvContent.setPadding(start, top, end, bottom)
        holder.rvContent.clipToPadding = false
        holder.tvTitle.text = title

        // --- More icon ---
        if (onMoreClick == null) {
            holder.btnMore.visibility = View.GONE
            holder.btnMore.setOnClickListener(null)
        } else {
            holder.btnMore.visibility = View.VISIBLE
            holder.btnMore.setOnClickListener { onMoreClick.invoke() }
        }

        // --- Content RecyclerView ---
        holder.rvContent.apply {
            layoutManager =
                if (isGrid) GridLayoutManager(context, 2)
                else LinearLayoutManager(context, orientation, false)

            adapter = innerAdapter
        }

        // --- Indicator (only when enabled + not grid) ---
        if (!showIndicator || isGrid) {
            holder.rvIndicator.visibility = View.GONE
            cleanupIndicator(holder)
            return
        }

        setupIndicator(holder)
    }

    private fun setupIndicator(holder: ViewHolder) {
        val rv = holder.rvContent
        val indicatorRv = holder.rvIndicator

        // dot adapter init
        val dots = holder.dotAdapter ?: DotIndicatorAdapter().also {
            holder.dotAdapter = it
            indicatorRv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.HORIZONTAL, false)
            indicatorRv.adapter = it
        }

        // attach snap helper once
        if (holder.snapHelper == null) {
            holder.snapHelper = androidx.recyclerview.widget.PagerSnapHelper().also { snap ->
                // tránh attach nhiều lần
                if (rv.onFlingListener == null) {
                    snap.attachToRecyclerView(rv)
                }
            }
        }

        // initial count
        val count = innerAdapter.itemCount
        dots.submitCount(count)
        indicatorRv.visibility = if (count > 1) View.VISIBLE else View.GONE

        // remove old listener
        holder.scrollListener?.let { rv.removeOnScrollListener(it) }

        // add scroll listener to update dot on idle
        val snap = holder.snapHelper!!
        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = snap.findSnapView(recyclerView.layoutManager) ?: return
                    val pos = recyclerView.layoutManager?.getPosition(snapView) ?: return
                    dots.setPosition(pos)
                }
            }
        }
        holder.scrollListener = listener
        rv.addOnScrollListener(listener)

        // observe innerAdapter data changes to update dot count
        if (holder.dataObserver == null) {
            val obs = object : RecyclerView.AdapterDataObserver() {
                private fun refresh() {
                    val newCount = innerAdapter.itemCount
                    dots.submitCount(newCount)
                    indicatorRv.visibility = if (newCount > 1) View.VISIBLE else View.GONE
                }
                override fun onChanged() = refresh()
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = refresh()
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = refresh()
            }
            holder.dataObserver = obs
            innerAdapter.registerAdapterDataObserver(obs)
        }
    }

    private fun cleanupIndicator(holder: ViewHolder) {
        // remove scroll listener
        holder.scrollListener?.let { holder.rvContent.removeOnScrollListener(it) }
        holder.scrollListener = null

        // unregister observer
        holder.dataObserver?.let { innerAdapter.unregisterAdapterDataObserver(it) }
        holder.dataObserver = null
    }

    override fun onViewRecycled(holder: ViewHolder) {
        cleanupIndicator(holder)
        super.onViewRecycled(holder)
    }
    data class SectionPadding(
        val startDp: Int = 0,
        val topDp: Int = 0,
        val endDp: Int = 0,
        val bottomDp: Int = 0
    )


    override fun getItemCount(): Int = if (isVisible) 1 else 0
}

