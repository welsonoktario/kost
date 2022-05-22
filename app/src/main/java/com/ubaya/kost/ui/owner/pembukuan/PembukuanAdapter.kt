package com.ubaya.kost.ui.owner.pembukuan

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ubaya.kost.ui.owner.pembukuan.invoices.InvoiceFragment
import com.ubaya.kost.ui.owner.pembukuan.pengeluaran.PengeluaranFragment

class PembukuanAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val fragments = arrayListOf(
        InvoiceFragment(),
        PengeluaranFragment()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}