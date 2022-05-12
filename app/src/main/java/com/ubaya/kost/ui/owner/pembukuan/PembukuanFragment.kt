package com.ubaya.kost.ui.owner.pembukuan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayoutMediator
import com.ubaya.kost.databinding.FragmentPembukuanBinding
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PembukuanFragment : Fragment() {
    private lateinit var adapter: PembukuanAdapter
    private lateinit var dateRangePicker: MaterialDatePicker<Pair<Long, Long>>

    private var _binding: FragmentPembukuanBinding? = null

    private val binding get() = _binding!!
    private val pembukuanViewModel by viewModels<PembukuanViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pembukuanViewModel.loadPembukuan()
        _binding = FragmentPembukuanBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() {
        adapter = PembukuanAdapter(childFragmentManager, lifecycle)
        binding.pembukuanVP.adapter = adapter

        val tabLayout = binding.pembukuanTL
        TabLayoutMediator(tabLayout, binding.pembukuanVP) { tab, position ->
            tab.text = when (position) {
                0 -> "Pemasukan"
                1 -> "Pengeluaran"
                else -> null
            }
        }.attach()

        binding.pembukuanFilter.setOnClickListener {
            openDateFilter()
        }
    }

    private fun openDateFilter() {
        if (!::dateRangePicker.isInitialized) {
            dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Pilih tanggal mulai dan akhir")
                    .setSelection(
                        Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                    .build()

            dateRangePicker.addOnPositiveButtonClickListener {
                val tz = TimeZone.currentSystemDefault()
                val df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                val startDate = Instant
                    .fromEpochMilliseconds(it.first)
                    .toLocalDateTime(tz)
                    .toJavaLocalDateTime()
                val endDate = Instant
                    .fromEpochMilliseconds(it.second)
                    .toLocalDateTime(tz)
                    .toJavaLocalDateTime()
                val start = startDate.format(df)
                val end = endDate.format(df)

                binding.pembukuanFilter.setText("$start - $end")
                pembukuanViewModel.startDate.value = start
                pembukuanViewModel.endDate.value = end
                pembukuanViewModel.loadPembukuan()
            }
        }

        dateRangePicker.show(childFragmentManager, "DATE_FILTER")
    }
}