package com.ubaya.kost.ui.owner.pembukuan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.data.models.Invoice
import com.ubaya.kost.databinding.DialogDetailInvoiceBinding
import com.ubaya.kost.databinding.FragmentInvoiceBinding
import com.ubaya.kost.ui.shared.invoices.InvoiceAdapter
import com.ubaya.kost.util.NumberUtil

class InvoiceFragment : Fragment(), InvoiceAdapter.InvoiceListener {

    private lateinit var invoices: ArrayList<Invoice>
    private lateinit var adapter: InvoiceAdapter
    private lateinit var dialog: AlertDialog

    private var _binding: FragmentInvoiceBinding? = null
    private var _dialogBinding: DialogDetailInvoiceBinding? = null

    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!
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
        _dialogBinding = DialogDetailInvoiceBinding.inflate(layoutInflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        initView()
    }

    override fun onCardInvoiceClick(position: Int) {
        openDialog(position)
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        invoices = arrayListOf()
        adapter = InvoiceAdapter(invoices, this)

        binding.invoiceRV.adapter = adapter
        binding.invoiceRV.layoutManager = layoutManager

        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Detail Tagihan")
            .setView(dialogBinding.root)
            .setPositiveButton("Tutup", null)
            .create()
    }

    private fun initObserver() {
        pembukuanViewModel.invoices.observe(viewLifecycleOwner) {
            invoices.clear()
            invoices.addAll(it)

            adapter.notifyDataSetChanged()

            binding.invoiceEmpty.visibility =
                if (!pembukuanViewModel.isLoading.value!! && invoices.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    private fun openDialog(position: Int) {
        var total = 0
        val invoice = pembukuanViewModel.invoices.value?.get(position)!!
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