package com.ubaya.kost.ui.owner.services

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
import com.ubaya.kost.data.models.TenantService
import com.ubaya.kost.databinding.FragmentServicesBinding

class ServicesFragment : Fragment(), ServicesAdapter.ServicesListener {

    private lateinit var tenantServices: ArrayList<TenantService>
    private lateinit var adapter: ServicesAdapter

    private var _binding: FragmentServicesBinding? = null

    private val binding get() = _binding!!
    private val serviceViewModel by navGraphViewModels<ServicesViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (serviceViewModel.tenantService.value!!.isEmpty()) {
            serviceViewModel.loadServices()
        }

        _binding = FragmentServicesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    private fun initView() {
        tenantServices = arrayListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = ServicesAdapter(tenantServices, this)

        binding.serviceRV.adapter = adapter
        binding.serviceRV.layoutManager = layoutManager
    }

    private fun initObserver() {
        serviceViewModel.tenantService.observe(viewLifecycleOwner) {
            tenantServices.clear()
            tenantServices.addAll(it)
            adapter.notifyDataSetChanged()
        }

        serviceViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.serviceRV.visibility = View.GONE
                binding.serviceLoading.visibility = View.VISIBLE
            } else {
                binding.serviceRV.visibility = View.VISIBLE
                binding.serviceLoading.visibility = View.GONE
            }
        }

        serviceViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(serviceViewModel.error.value!!.msg)
                    .show()
                serviceViewModel.error.value = Error()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onListClick(position: Int) {
        if (tenantServices[position].status == "pending") {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Apakah anda yakin ingin menyetujui permintaan pemesanan service ini?")
                .setPositiveButton("Terima") { _, _ ->
                    serviceViewModel.updateService(
                        position,
                        "diterima"
                    )
                }
                .setNegativeButton("Tolak") { _, _ ->
                    serviceViewModel.updateService(
                        position,
                        "ditolak"
                    )
                }
                .setNeutralButton("Batal", null)
                .show()
        }
    }
}