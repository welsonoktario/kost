package com.ubaya.kost.ui.owner.denda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ubaya.kost.R
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.databinding.DialogEditDendaBinding
import com.ubaya.kost.databinding.FragmentDendaBinding
import com.ubaya.kost.util.NumberUtil

class DendaFragment : Fragment() {
    private lateinit var tenants: ArrayList<Tenant>
    private lateinit var adapter: DendaAdapter
    private lateinit var dialog: AlertDialog

    private var _binding: FragmentDendaBinding? = null
    private var _dialogBinding: DialogEditDendaBinding? = null

    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!
    private val dendaViewModel by navGraphViewModels<DendaViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dendaViewModel.loadDenda()
        _binding = FragmentDendaBinding.inflate(inflater, container, false)
        _dialogBinding = DialogEditDendaBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    private fun initView() {
        tenants = dendaViewModel.tenants.value!!
        adapter = DendaAdapter(tenants, Global.authKost)
        val layouManager = LinearLayoutManager(requireContext())

        binding.dendaRV.let {
            it.adapter = adapter
            it.layoutManager = layouManager
        }

        binding.dendaFAB.setOnClickListener {
            openDendaDialog()
        }
    }

    private fun initObserver() {
        dendaViewModel.tenants.observe(viewLifecycleOwner) {
            tenants.clear()
            tenants.addAll(it)

            adapter.notifyDataSetChanged()

            binding.dendaFAB.isEnabled = tenants.isEmpty()

            binding.dendaEmpty.visibility =
                if (!dendaViewModel.isLoading.value!! && tenants.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        dendaViewModel.kost.observe(viewLifecycleOwner) { kost ->
            dialogBinding.dialogDendaNominalText.text = if (kost.nominalDenda != null) {
                NumberUtil().rupiah(kost.nominalDenda!!)
            } else {
                "-"
            }

            if (kost.nominalDenda != null) {
                dialogBinding.dialogDendaNominal.setText(kost.nominalDenda!!.toString())
            }

            dialogBinding.dialogDendaIntervalText.text = kost.intervalDenda.toString()
            dialogBinding.dialogDendaInterval.setText(kost.intervalDenda.toString())
            dialogBinding.dialogDendaBerlaku.setText(kost.dendaBerlaku.toString())
        }

        dendaViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.dendaRV.visibility = View.GONE
                binding.dendaFAB.visibility = View.GONE
                binding.dendaLoading.visibility = View.VISIBLE
            } else {
                binding.dendaRV.visibility = View.VISIBLE
                binding.dendaFAB.visibility = View.VISIBLE
                binding.dendaLoading.visibility = View.GONE
            }
        }

        dendaViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(dendaViewModel.error.value!!.msg)
                    .show()
                dendaViewModel.error.value = Error()
            }
        }

        dendaViewModel.msg.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Snackbar.make(binding.dendaLayout, it, Snackbar.LENGTH_SHORT).show()
                dendaViewModel.msg.value = ""
            }
        }
    }

    private fun openDendaDialog() {
        if (!::dialog.isInitialized) {
            dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pengaturan Denda")
                .setView(dialogBinding.root)
                .setPositiveButton("Edit") { _, _ -> updateDenda() }
                .setNegativeButton("Batal", null)
                .create()
        }

        dialog.show()
    }

    private fun updateDenda() {
        val nominal = dialogBinding.dialogDendaNominal.text.toString().toInt()
        val interval = dialogBinding.dialogDendaInterval.text.toString().toInt()
        val berlaku = dialogBinding.dialogDendaBerlaku.text.toString().toInt()

        dendaViewModel.updateDenda(nominal, interval, berlaku)
    }
}