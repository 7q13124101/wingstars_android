import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R

// SectionTitleAdapter.kt
class SectionTitleAdapter(
    private val title: String,
    private val onClick: () -> Unit
) : RecyclerView.Adapter<SectionTitleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvSectionTitle)
        val root: View = view.findViewById(R.id.ivSectionMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Sử dụng lại layout section_title_with_more có sẵn của bạn
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_title_with_more, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = title
        holder.root.setOnClickListener { onClick() }
    }

    override fun getItemCount() = 1
}