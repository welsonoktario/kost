package com.ubaya.kost.ui.owner.pembukuan

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.ubaya.kost.R
import com.ubaya.kost.databinding.FragmentPembukuanBinding

class PembukuanFragment : Fragment() {
    private lateinit var adapter: PembukuanAdapter

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.owner_pembukuan_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_pengeluaran -> {}
        }

        return super.onOptionsItemSelected(item)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}