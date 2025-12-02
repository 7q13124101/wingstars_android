package com.wingstars.member.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.member.adapter.PersonalScheduleItemAdapter
import com.wingstars.member.adapter.ScheduleFunBean
import com.wingstars.member.adapter.SelectTeamAdapter
import com.wingstars.member.adapter.SelectTeamFunBean
import com.wingstars.member.databinding.FragmentPersonalScheduleBinding
import com.wingstars.member.viewmodel.PersonalScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class PersonalScheduleFragment : Fragment() {

    private var wingStarsMonth = SimpleDateFormat("yyyy/MM").format(Date())
    private lateinit var binding: FragmentPersonalScheduleBinding
    private lateinit var viewModel: PersonalScheduleViewModel

    private lateinit var teamCategoryAdapter: SelectTeamAdapter

    private lateinit var personalScheduleItemAdapter: PersonalScheduleItemAdapter

    private var isDataLoaded = false // 标记数据是否加载过
    override fun onResume() {
        super.onResume()

        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun loadData() {
        viewModel.getTeamCategoryList()
        viewModel.getPersonalScheduleList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[PersonalScheduleViewModel::class.java]

        wingStarsMonth = arguments?.getString("wing_stars_month").toString()
        viewModel.getWingStarsScheduleJson(wingStarsMonth)

        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun changeMonth(dateMonth: String, addMonths: Int): String {
        // 将 dateMonth 字符串解析为 Date 对象
        val sdf = SimpleDateFormat("yyyy/MM")
        val date = sdf.parse(dateMonth)

        // 获取 Calendar 实例
        val calendar = Calendar.getInstance()
        calendar.time = date

        // 加上或减去月份
        calendar.add(Calendar.MONTH, addMonths)

        // 返回更新后的月份字符串
        return sdf.format(calendar.time)
    }

    private fun dateEvent() {
        binding.tvDate.text = wingStarsMonth
        binding.ivPrev.setOnClickListener {
            wingStarsMonth = changeMonth(wingStarsMonth, -1)
            binding.tvDate.text = wingStarsMonth

            if (viewModel.dataDTOArrayList.value.isNullOrEmpty()) {
                viewModel.getWingStarsScheduleJson(wingStarsMonth)
            } else {
                viewModel.getOtherMonthData(wingStarsMonth)
            }
        }
        binding.ivNext.setOnClickListener {
            wingStarsMonth = changeMonth(wingStarsMonth, 1)
            binding.tvDate.text = wingStarsMonth

            if (viewModel.dataDTOArrayList.value.isNullOrEmpty()) {
                viewModel.getWingStarsScheduleJson(wingStarsMonth)
            } else {
                viewModel.getOtherMonthData(wingStarsMonth)
            }
        }
    }

    private fun initView() {

        //init Date event fun
        dateEvent()

        //create team category adapter.
        teamCategoryAdapter = SelectTeamAdapter(
            requireActivity(),
            mutableListOf(),
            object : SelectTeamAdapter.OnItemListener {
                override fun onItemClick(data: SelectTeamFunBean, position: Int) {
                    viewModel.setScheduleListByTeam(data)
                }
            })
        binding.rvTeamList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTeamList.adapter = teamCategoryAdapter

        //set team category adapter data.
        viewModel.teamCategoryList.observe(viewLifecycleOwner) {
            teamCategoryAdapter.setList(it)
            viewModel.setScheduleListByTeam(it[0])
        }

        //create personal Schedule adapter.
        personalScheduleItemAdapter =
            PersonalScheduleItemAdapter(requireActivity(), mutableListOf())
        binding.rvWsSchedule.adapter = personalScheduleItemAdapter

        //set personal Schedule adapter data.
        viewModel.personalScheduleList.observe(viewLifecycleOwner) {
            setScheduleListData(it)
        }


        binding.srlPersonalScheduleRecord.setOnRefreshListener {
            binding.srlPersonalScheduleRecord.finishRefresh()
        }

    }

    private fun setScheduleListData(it: MutableList<ScheduleFunBean>?) {
        if (it.isNullOrEmpty()) {
            binding.slWsSchedule.visibility = View.GONE
            binding.llWsWeeklyEmpty.visibility = View.VISIBLE

        } else {
            personalScheduleItemAdapter.setList(it)
            binding.slWsSchedule.visibility = View.VISIBLE
            binding.llWsWeeklyEmpty.visibility = View.GONE
            binding.scrollView.scrollTo(0, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}