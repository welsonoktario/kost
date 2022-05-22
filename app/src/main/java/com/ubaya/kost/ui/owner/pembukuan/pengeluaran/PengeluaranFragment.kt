package com.ubaya.kost.ui.owner.pembukuan.pengeluaran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.data.models.Pengeluaran
import com.ubaya.kost.databinding.DialogAddPengeluaranBinding
import com.ubaya.kost.databinding.FragmentPengeluaranBinding
import com.ubaya.kost.ui.owner.pembukuan.PembukuanViewModel
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.format.DateTimeFormatter
import java.util.Locale

class PengeluaranFragment : Fragment() {

    private lateinit var pengeluarans: ArrayList<Pengeluaran>
    private lateinit var adapter: PengeluaranAdapter
    private lateinit var dialogBinding: DialogAddPengeluaranBinding
    private lateinit var dialog: AlertDialog

    private var _binding: FragmentPengeluaranBinding? = null

    private val binding get() = _binding!!
    private val pembukuanViewModel: PembukuanViewModel by viewModels(
        ownerProducer = {
            requireParentFragment()
        }
    )
    private val tz = TimeZone.currentSystemDefault()
    private val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengeluaranBinding.inflate(layoutInflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        initView()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        pengeluarans = arrayListOf()
        adapter = PengeluaranAdapter(pengeluarans)

        binding.pengeluaranRV.adapter = adapter
        binding.pengeluaranRV.layoutManager = layoutManager
        binding.pengeluaranAdd.setOnClickListener {
            openDialog()
        }
    }

    private fun initObserver() {
        pembukuanViewModel.startDate.observe(viewLifecycleOwner) {
            pembukuanViewModel.loadPembukuan()
        }

        pembukuanViewModel.pengeluarans.observe(viewLifecycleOwner) {
            pengeluarans.clear()
            pengeluarans.addAll(it)

            adapter.notifyDataSetChanged()

            binding.pengeluaranEmpty.visibility =
                if (!pembukuanViewModel.isLoading.value!! && pengeluarans.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    private fun openDialog() {
        if (!::dialogBinding.isInitialized) {
            dialogBinding = DialogAddPengeluaranBinding.inflate(layoutInflater, binding.root, false)
            dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Tambah Pengeluaran")
                .setView(dialogBinding.root)
                .setPositiveButton("Tambah") { dialog, _ -> addPengeluaran() }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .create()
        }


        val today = Clock.System.todayAt(tz).toJavaLocalDate()
        val formattedDate = today.format(df)
        dialogBinding.dialogAddPengeluaranTanggal.setText(formattedDate)
        dialogBinding.dialogAddPengeluaranDeskripsi.text!!.clear()
        dialogBinding.dialogAddPengeluaranNominal.text!!.clear()
        dialogBinding.dialogAddPengeluaranTanggal.setOnClickListener {
            openDatePicker()
        }
        dialog.show()
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Tanggal Pengeluaran")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(childFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener {
            val instant = Instant.fromEpochMilliseconds(it)
            val date = instant.toLocalDateTime(tz).date
            val formattedDate = date.toJavaLocalDate().format(df)

            dialogBinding.dialogAddPengeluaranTanggal.setText(formattedDate)
        }
    }

    private fun addPengeluaran() {
        val date = dialogBinding.dialogAddPengeluaranTanggal.text.toString()
        val description = dialogBinding.dialogAddPengeluaranDeskripsi.text.toString()
        val nominal = dialogBinding.dialogAddPengeluaranNominal.text.toString().toInt()
        pembukuanViewModel.addPengeluaran(date, description, nominal)
    }
}