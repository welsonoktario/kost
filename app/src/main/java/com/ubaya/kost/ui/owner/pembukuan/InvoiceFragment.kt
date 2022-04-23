package com.ubaya.kost.ui.owner.pembukuan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubaya.kost.data.models.Invoice
import com.ubaya.kost.databinding.FragmentInvoiceBinding

class InvoiceFragment : Fragment(), InvoiceAdapter.InvoiceListener {

    private lateinit var invoices: ArrayList<Invoice>
    private lateinit var adapter: InvoiceAdapter

    private var _binding: FragmentInvoiceBinding? = null

    private val binding get() = _binding!!
    private val pembukuanViewModel: PembukuanViewModel by viewModels(
        ownerProducer = {
            requireParentFragment()
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoiceBinding.inflate(layoutInflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        initView()
    }

    override fun onCardInvoiceClick(position: Int) {
        //
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        invoices = arrayListOf()
        adapter = InvoiceAdapter(invoices, this)

        binding.invoiceRV.adapter = adapter
        binding.invoiceRV.layoutManager = layoutManager
    }

    private fun initObserver() {
        pembukuanViewModel.invoices.observe(viewLifecycleOwner) {
            Log.d("INVOICES", it.toString())
            invoices.clear()
            invoices.addAll(it)

            adapter.notifyDataSetChanged()
        }
    }
}