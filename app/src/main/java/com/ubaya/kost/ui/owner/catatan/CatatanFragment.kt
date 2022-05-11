package com.ubaya.kost.ui.owner.catatan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Catatan
import com.ubaya.kost.databinding.DialogAddCatatanBinding
import com.ubaya.kost.databinding.FragmentCatatanBinding
import kotlinx.datetime.*
import java.time.format.DateTimeFormatter
import java.util.*

class CatatanFragment : Fragment() {

    private lateinit var catatans: ArrayList<Catatan>
    private lateinit var adapter: CatatanAdapter
    private lateinit var dialog: AlertDialog

    private var _binding: FragmentCatatanBinding? = null
    private var _dialogBinding: DialogAddCatatanBinding? = null

    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!
    private val catatanViewModel by navGraphViewModels<CatatanViewModel>(R.id.mobile_navigation)

    private val tz = kotlinx.datetime.TimeZone.currentSystemDefault()
    private val df: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        catatanViewModel.loadCatatan()
        _binding = FragmentCatatanBinding.inflate(inflater, container, false)
        _dialogBinding = DialogAddCatatanBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() {
        catatans = catatanViewModel.catatans.value!!
        adapter = CatatanAdapter(catatans)
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Catatan")
            .setView(dialogBinding.root)
            .setPositiveButton("Tambah") { _, _ -> addCatatan() }
            .setNegativeButton("Batal", null)
            .create()

        binding.catatanRV.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.catatanAdd.setOnClickListener {
            val today = Clock.System.todayAt(tz).toJavaLocalDate()
            val formattedDate = today.format(df)
            dialogBinding.dialogAddCatatanTanggal.setText(formattedDate)
            dialogBinding.dialogAddCatatanDeskripsi.text!!.clear()
            dialog.show()
        }

        dialogBinding.dialogAddCatatanTanggal.setOnClickListener {
            openDatePicker()
        }
    }

    private fun initObserver() {
        catatanViewModel.catatans.observe(viewLifecycleOwner) {
            catatans.clear()
            catatans.addAll(it)

            adapter.notifyDataSetChanged()

            binding.catatanEmpty.visibility =
                if (!catatanViewModel.isLoading.value!! && catatans.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        catatanViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.catatanRV.visibility = View.GONE
                binding.catatanAdd.visibility = View.GONE
                binding.catatanEmpty.visibility = View.GONE
                binding.catatanLoading.visibility = View.VISIBLE
            } else {
                binding.catatanRV.visibility = View.VISIBLE
                binding.catatanAdd.visibility = View.VISIBLE
                binding.catatanLoading.visibility = View.GONE
            }
        }

        catatanViewModel.msg.observe(viewLifecycleOwner) {
            if (it != null) {
                Snackbar.make(binding.catatanLayout, it, Snackbar.LENGTH_SHORT)
                    .setAction("OK", null)
                    .show()
                catatanViewModel.msg.value = null
            }
        }
    }

    private fun addCatatan() {
        val date = dialogBinding.dialogAddCatatanTanggal.text.toString()
        val description = dialogBinding.dialogAddCatatanDeskripsi.text.toString()

        catatanViewModel.addCatatan(description, date)
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Tanggal Konfirmasi")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(childFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener {
            val instant = Instant.fromEpochMilliseconds(it)
            val date = instant.toLocalDateTime(tz).date
            val formattedDate = date.toJavaLocalDate().format(df)

            dialogBinding.dialogAddCatatanTanggal.setText(formattedDate)
        }
    }
}