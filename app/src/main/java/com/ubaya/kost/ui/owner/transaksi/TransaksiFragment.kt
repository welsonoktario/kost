package com.ubaya.kost.ui.owner.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Invoice
import com.ubaya.kost.databinding.FragmentTransaksiBinding
import com.ubaya.kost.ui.shared.invoices.InvoiceAdapter

class TransaksiFragment : Fragment(), InvoiceAdapter.InvoiceListener {
    private lateinit var invoices: ArrayList<Invoice>
    private lateinit var adapter: InvoiceAdapter
    private var _binding: FragmentTransaksiBinding? = null

    private val binding get() = _binding!!
    private val transaksiViewModel by navGraphViewModels<TransaksiViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        transaksiViewModel.loadTransaksi()
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCardInvoiceClick(position: Int) {
        //
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        invoices = arrayListOf()
        adapter = InvoiceAdapter(invoices, this)

        binding.transaksiRV.adapter = adapter
        binding.transaksiRV.layoutManager = layoutManager
    }

    private fun initObserver() {
        transaksiViewModel.invoices.observe(viewLifecycleOwner) {
            invoices.clear()
            invoices.addAll(it)

            adapter.notifyDataSetChanged()

            binding.transaksiEmpty.visibility =
                if (!transaksiViewModel.isLoading.value!! && invoices.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        transaksiViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.transaksiRV.visibility = View.GONE
                binding.transaksiLoading.visibility = View.VISIBLE
            } else {
                binding.transaksiRV.visibility = View.VISIBLE
                binding.transaksiLoading.visibility = View.GONE
            }
        }

        transaksiViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(transaksiViewModel.error.value!!.msg)
                    .show()
                transaksiViewModel.error.value = Error()
            }
        }
    }
}