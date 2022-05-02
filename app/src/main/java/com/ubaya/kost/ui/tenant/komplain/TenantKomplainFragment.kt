package com.ubaya.kost.ui.tenant.komplain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Complain
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.databinding.DialogTenantAddKomplainBinding
import com.ubaya.kost.databinding.FragmentTenantKomplainBinding
import com.ubaya.kost.ui.owner.komplain.KomplainAdapter

class TenantKomplainFragment : Fragment() {
    private lateinit var complains: ArrayList<Complain>
    private lateinit var adapter: KomplainAdapter
    private lateinit var dialogTenantAddKomplainBinding: DialogTenantAddKomplainBinding

    private var _binding: FragmentTenantKomplainBinding? = null

    private val binding get() = _binding!!
    private val tenantKomplainViewModel by navGraphViewModels<TenantKomplainViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tenantKomplainViewModel.loadComplains()
        _binding = FragmentTenantKomplainBinding.inflate(inflater, container, false)
        dialogTenantAddKomplainBinding =
            DialogTenantAddKomplainBinding.inflate(inflater, container, false)

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
        complains = arrayListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = KomplainAdapter(complains)

        binding.tenantKomplainRV.adapter = adapter
        binding.tenantKomplainRV.layoutManager = layoutManager

        binding.tenantKomplainAddKomplain.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setView(dialogTenantAddKomplainBinding.root)
                .setTitle("Tambah Komplain")
                .setPositiveButton("Tambah") { _, _ ->
                    tenantKomplainViewModel.addComplain(
                        dialogTenantAddKomplainBinding
                            .dialogTenantAddKomplainKomplain
                            .text
                            .toString()
                    )
                }
                .setNegativeButton("Batal", null)
                .setCancelable(false)
                .show()
        }
    }

    private fun initObserver() {
        tenantKomplainViewModel.complains.observe(viewLifecycleOwner) {
            complains.clear()
            complains.addAll(it)
            adapter.notifyDataSetChanged()
        }

        tenantKomplainViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.tenantKomplainRV.visibility = View.GONE
                binding.tenantKomplainAddKomplain.visibility = View.GONE
                binding.tenantKomplainLoading.visibility = View.VISIBLE
            } else {
                binding.tenantKomplainRV.visibility = View.VISIBLE
                binding.tenantKomplainAddKomplain.visibility = View.VISIBLE
                binding.tenantKomplainLoading.visibility = View.GONE
            }
        }

        tenantKomplainViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(tenantKomplainViewModel.error.value!!.msg)
                    .show()
                tenantKomplainViewModel.error.value = Error()
            }
        }
    }
}