package com.ubaya.kost.ui.tenant.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.ubaya.kost.R
import com.ubaya.kost.data.Global
import com.ubaya.kost.databinding.DialogFotoBinding
import com.ubaya.kost.databinding.FragmentTenantHomeBinding
import com.ubaya.kost.util.PrefManager
import com.ubaya.kost.util.VolleyClient

class TenantHomeFragment : Fragment() {
    private var _binding: FragmentTenantHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialogFotoBinding: DialogFotoBinding
    private lateinit var dialogFoto: AlertDialog

    private val tenantViewModel: TenantHomeViewModel by navGraphViewModels(R.id.mobile_navigation)

    private val user = Global.authUser
    private val tenant = Global.authTenant

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentTenantHomeBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (tenantViewModel.roomType.value == null) {
            tenantViewModel.loadDetailTenant(tenant.id)
        }
        tenantViewModel.msg.value = null

        initObserver()
        initView()
    }

    private fun initView() {
        binding.homeTenantNama.text = user.name
        binding.homeTenantPhone.text = user.phone
        binding.homeTenantTglMasuk.text = tenant.entryDate
        binding.homeTenantDue.text = tenant.dueDate

        if (tenant.diffFromDue() > 7) {
            binding.cardTagihan.visibility = View.GONE
        }

        binding.btnFoto.setOnClickListener {
            fotoIdentitas()
        }

        binding.btnHomeTenantService.setOnClickListener {
//            btnKonfirm()
        }

        binding.btnHomeTenantKomplain.setOnClickListener {
//            btnTambah()
        }
    }

    private fun initObserver() {
        tenantViewModel.isLoading.observe(viewLifecycleOwner) {
            if (!it) {
                binding.homeTenantLoading.visibility = View.GONE
                binding.homeTenantLayout.visibility = View.VISIBLE
            } else {
                binding.homeTenantLoading.visibility = View.VISIBLE
                binding.homeTenantLayout.visibility = View.GONE
            }
        }

        tenantViewModel.roomType.observe(viewLifecycleOwner) {
            binding.homeTenantTipeKamar.text = it.name
        }

        tenantViewModel.services.observe(viewLifecycleOwner) {
            binding.homeTenantListService.removeAllViews()
            it.forEach { service ->
                val chip = Chip(
                    context,
                    null,
                    R.style.Widget_MaterialComponents_Chip_Choice
                )
                chip.id = ViewCompat.generateViewId()
                chip.tag = "${service.id}"
                chip.text = service.name
                chip.setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.onPrimary
                    )
                )
                chip.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.primary)

                binding.homeTenantListService.addView(chip)
            }
        }

        tenantViewModel.msg.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Snackbar.make(binding.homeTenantLayoutMain, it, Snackbar.LENGTH_SHORT)
                    .setAction("OK") { }
                    .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tenant_home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_tenant_notifications -> findNavController().navigate(
                R.id.action_fragment_tenant_home_to_fragment_tenant_notification
            )
            R.id.menu_tenant_message -> findNavController().navigate(
                R.id.action_fragment_tenant_home_to_fragment_tenant_notification
            )
            R.id.menu_tenant_logout -> logout()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun fotoIdentitas() {
        if (!this::dialogFotoBinding.isInitialized) {
            dialogFotoBinding = DialogFotoBinding.inflate(layoutInflater, binding.root, false)
        }

        if (!this::dialogFoto.isInitialized) {
            dialogFotoBinding.fotoKtp.load("${VolleyClient.BASE_URL}/storage/${tenant.id}")
            dialogFoto = AlertDialog.Builder(requireContext())
                .setView(dialogFotoBinding.root)
                .create()
        }

        dialogFotoBinding.fotoKtp.load("${VolleyClient.BASE_URL}/storage/${tenant.ktp}")
        dialogFoto.show()
    }

    private fun logout() {
        val prefs = PrefManager.getInstance(requireContext())
        prefs.clear()

        findNavController().popBackStack(R.id.fragment_tenant_home, true)
        findNavController().navigate(R.id.fragment_login)
    }
}