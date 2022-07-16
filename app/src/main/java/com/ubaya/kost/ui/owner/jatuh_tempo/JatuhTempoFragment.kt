package com.ubaya.kost.ui.owner.jatuh_tempo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.databinding.FragmentJatuhTempoBinding

class JatuhTempoFragment : Fragment() {

    private lateinit var tenants: ArrayList<Tenant>
    private lateinit var adapter: JatuhTempoAdapter

    private var _binding: FragmentJatuhTempoBinding? = null

    private val binding get() = _binding!!
    private val jatuhTempoViewModel by navGraphViewModels<JatuhTempoViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        jatuhTempoViewModel.loadTenants()
        _binding = FragmentJatuhTempoBinding.inflate(layoutInflater, container, false)

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
        tenants = jatuhTempoViewModel.tenants.value!!
        adapter = JatuhTempoAdapter(tenants)

        binding.jatuhTempoRV.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObserver() {
        jatuhTempoViewModel.tenants.observe(viewLifecycleOwner) {
            tenants.clear()
            tenants.addAll(it)

            adapter.notifyDataSetChanged()

            binding.jatuhTempoEmpty.visibility =
                if (!jatuhTempoViewModel.isLoading.value!! && tenants.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        jatuhTempoViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.jatuhTempoRV.visibility = View.GONE
                binding.jatuhTempoEmpty.visibility = View.GONE
                binding.jatuhTempoLoading.visibility = View.VISIBLE
            } else {
                binding.jatuhTempoRV.visibility = View.VISIBLE
                binding.jatuhTempoLoading.visibility = View.GONE
            }
        }

        jatuhTempoViewModel.msg.observe(viewLifecycleOwner) {
            if (it != null) {
                Snackbar.make(binding.jatuhTempoLayout, it, Snackbar.LENGTH_SHORT)
                    .setAction("OK", null)
                    .show()
                jatuhTempoViewModel.msg.value = null
            }
        }
    }
}