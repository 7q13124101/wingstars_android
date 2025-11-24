import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wingstars.count.fragment.HaveUsedFragment
import com.wingstars.count.fragment.NotUsedFragment


class ExchangeHistoryAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NotUsedFragment()
            1 -> HaveUsedFragment()
            else -> NotUsedFragment()
        }
    }
}