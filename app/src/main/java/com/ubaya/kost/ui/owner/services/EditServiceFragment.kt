package com.ubaya.kost.ui.owner.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Service
import com.ubaya.kost.databinding.DialogDetailServiceBinding
import com.ubaya.kost.databinding.FragmentEditServiceBinding

class EditServiceFragment : Fragment(), EditServiceAdapter.EditServiceListener {
    private lateinit var adapter: EditServiceAdapter
    private lateinit var services: ArrayList<Service>
    private lateinit var dialog: AlertDialog

    private var _binding: FragmentEditServiceBinding? = null
    private var _dialogBinding: DialogDetailServiceBinding? = null

    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!
    private val servicesViewModel by navGraphViewModels<ServicesViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        servicesViewModel.loadServices()
        _binding = FragmentEditServiceBinding.inflate(layoutInflater, container, false)
        _dialogBinding = DialogDetailServiceBinding.inflate(layoutInflater, container, false)

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
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setNegativeButton("Batal", null)
            .create()

        services = servicesViewModel.services.value!!
        adapter = EditServiceAdapter(services, this)

        binding.editServiceRV.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.editServiceFabAdd.setOnClickListener {
            add()
        }
    }

    private fun initObserver() {
        servicesViewModel.services.observe(viewLifecycleOwner) {
            services.clear()
            services.addAll(it)
            adapter.notifyDataSetChanged()
        }

        servicesViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(servicesViewModel.error.value!!.msg)
                    .show()
                servicesViewModel.error.value = Error()
            }
        }
    }

    override fun onCardEditServiceClick(position: Int) {
        val service = services[position]

        dialogBinding.dialogServiceName.setText(service.name)
        dialogBinding.dialogServiceDescription.setText(service.description)
        dialogBinding.dialogServiceCost.setText(service.cost.toString())

        dialogBinding.dialogServiceCost.isEnabled = servicesViewModel.tenantService.value!!.isEmpty()

        dialog.setTitle("Edit Service")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Simpan") { _, _ ->
            val id = service.id!!
            val name = dialogBinding.dialogServiceName.text.toString()
            val description = dialogBinding.dialogServiceDescription.text.toString()
            val cost = dialogBinding.dialogServiceCost.text.toString().toInt()

            servicesViewModel.updateService(id, name, description, cost)
        }
        dialog.show()
    }

    private fun add() {
        dialogBinding.dialogServiceName.text!!.clear()
        dialogBinding.dialogServiceDescription.text!!.clear()
        dialogBinding.dialogServiceCost.text!!.clear()
        dialogBinding.dialogServiceCost.isEnabled = true

        dialog.setTitle("Tambah Service")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Tambah") { _, _ ->
            val name = dialogBinding.dialogServiceName.text.toString()
            val description = dialogBinding.dialogServiceDescription.text.toString()
            val cost = dialogBinding.dialogServiceCost.text.toString().toInt()

            servicesViewModel.addService(name, description, cost)
        }
        dialog.show()
    }
}