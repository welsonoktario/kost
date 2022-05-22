package com.ubaya.kost.ui.owner.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Transaksi
import com.ubaya.kost.databinding.DialogDetailInvoiceBinding
import com.ubaya.kost.databinding.FragmentTransaksiBinding
import com.ubaya.kost.util.NumberUtil

class TransaksiFragment : Fragment(), TransaksiAdapter.TransaksiListener {
    private lateinit var transaksis: ArrayList<Transaksi>
    private lateinit var adapter: TransaksiAdapter
    private lateinit var dialog: AlertDialog

    private var _binding: FragmentTransaksiBinding? = null
    private var _dialogBinding: DialogDetailInvoiceBinding? = null

    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!

    private val transaksiViewModel by navGraphViewModels<TransaksiViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        transaksiViewModel.loadTransaksi()
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        _dialogBinding = DialogDetailInvoiceBinding.inflate(layoutInflater, container, false)

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

    override fun onCardTransaksiClick(position: Int) {
        openDialog(position)
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        transaksis = arrayListOf()
        adapter = TransaksiAdapter(transaksis, this)

        binding.transaksiRV.adapter = adapter
        binding.transaksiRV.layoutManager = layoutManager

        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Detail Tagihan")
            .setView(dialogBinding.root)
            .setPositiveButton("Tutup", null)
            .create()
    }

    private fun initObserver() {
        transaksiViewModel.transaksis.observe(viewLifecycleOwner) {
            transaksis.clear()
            transaksis.addAll(it)

            adapter.notifyDataSetChanged()

            binding.transaksiEmpty.visibility =
                if (!transaksiViewModel.isLoading.value!! && transaksis.isEmpty()) {
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

    private fun openDialog(position: Int) {
        var total = 0
        val invoice = transaksiViewModel.transaksis.value?.get(position)!!.invoice!!
        val tenant = invoice.tenant

        dialogBinding.dialogInvoiceTanggal.text = tenant.tanggalTagihan()

        if (invoice.invoiceDetails.isEmpty()) {
            dialogBinding.dialogInvoiceNull.visibility = View.VISIBLE
            dialogBinding.dialogInvoiceDetails.removeAllViews()
        } else {
            dialogBinding.dialogInvoiceNull.visibility = View.GONE
            dialogBinding.dialogInvoiceDetails.removeAllViews()

            invoice.invoiceDetails.forEach {
                val row = LinearLayout(requireContext())
                row.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                row.orientation = LinearLayout.HORIZONTAL
                total += it.cost

                val name = TextView(requireContext())
                name.text = it.description

                val price = TextView(requireContext())
                price.text = NumberUtil().rupiah(it.cost)

                row.addView(
                    name,
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                )
                row.addView(price)

                dialogBinding.dialogInvoiceDetails.addView(row)
            }
        }

        dialogBinding.dialogInvoiceTotal.text = NumberUtil().rupiah(total)
        dialog.show()
    }
}