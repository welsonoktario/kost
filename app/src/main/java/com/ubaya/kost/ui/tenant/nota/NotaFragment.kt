package com.ubaya.kost.ui.tenant.nota

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
import com.ubaya.kost.data.models.Invoice
import com.ubaya.kost.databinding.DialogDetailInvoiceBinding
import com.ubaya.kost.databinding.FragmentNotaBinding
import com.ubaya.kost.util.NumberUtil

class NotaFragment : Fragment(), NotaAdapter.NotaListener {

    private lateinit var invoices: ArrayList<Invoice>
    private lateinit var adapter: NotaAdapter
    private lateinit var dialog: AlertDialog

    private var _binding: FragmentNotaBinding? = null
    private var _dialogBinding: DialogDetailInvoiceBinding? = null

    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!
    private val notaViewModel by navGraphViewModels<NotaViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notaViewModel.loadNota()
        _binding = FragmentNotaBinding.inflate(inflater, container, false)
        _dialogBinding = DialogDetailInvoiceBinding.inflate(layoutInflater, container, false)

        return _binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        _dialogBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    override fun onCardNotaClick(position: Int) {
        openDialog(position)
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        invoices = arrayListOf()
        adapter = NotaAdapter(invoices, this)

        binding.notaRV.adapter = adapter
        binding.notaRV.layoutManager = layoutManager

        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Detail Tagihan")
            .setView(dialogBinding.root)
            .setPositiveButton("Tutup", null)
            .create()
    }

    private fun initObserver() {
        notaViewModel.invoices.observe(viewLifecycleOwner) {
            invoices.clear()
            invoices.addAll(it)

            adapter.notifyDataSetChanged()

            binding.notaEmpty.visibility =
                if (!notaViewModel.isLoading.value!! && invoices.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        notaViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.notaLoading.visibility = View.VISIBLE
                binding.notaRV.visibility = View.GONE
                binding.notaEmpty.visibility = View.GONE
            } else {
                binding.notaLoading.visibility = View.GONE
                binding.notaRV.visibility = View.VISIBLE
                binding.notaEmpty.visibility = View.VISIBLE
            }
        }

        notaViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(notaViewModel.error.value!!.msg)
                    .show()
                notaViewModel.error.value = Error()
            }
        }
    }

    private fun openDialog(position: Int) {
        var total = 0
        val invoice = notaViewModel.invoices.value?.get(position)!!
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