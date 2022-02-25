package com.ubaya.kost.ui.owner.dashboard.tenant

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import coil.load
import com.google.android.material.chip.Chip
import com.ubaya.kost.R
import com.ubaya.kost.databinding.DialogAddTagihanBinding
import com.ubaya.kost.databinding.DialogFotoBinding
import com.ubaya.kost.databinding.FragmentDetailTenantBinding
import com.ubaya.kost.util.VolleyClient
import org.json.JSONObject

class DetailTenantFragment : Fragment() {
    private var _binding: FragmentDetailTenantBinding? = null

    private lateinit var dialogFotoBinding: DialogFotoBinding
    private lateinit var dialogFoto: AlertDialog

    private lateinit var dialogAddTagihanBinding: DialogAddTagihanBinding
    private lateinit var dialogAddTagihan: AlertDialog

    private val binding get() = _binding!!
    private val args: DetailTenantFragmentArgs by navArgs()
    private val tenantViewModel: TenantViewModel by navGraphViewModels(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTenantBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tenantViewModel.loadDetailTenant(args.room)
        initObserver()
        initView()
    }

    private fun initView() {
        binding.btnTambah.setOnClickListener {
            btnTambah()
        }

        binding.btnFoto.setOnClickListener {
            fotoIdentitas()
        }
    }

    private fun initObserver() {
        tenantViewModel.isLoading.observe(viewLifecycleOwner) {
            if (!it) {
                binding.detailTenantLoading.visibility = View.GONE
                binding.detailTenantLayout.visibility = View.VISIBLE
            } else {
                binding.detailTenantLoading.visibility = View.VISIBLE
                binding.detailTenantLayout.visibility = View.GONE
            }
        }

        tenantViewModel.roomType.observe(viewLifecycleOwner) {
            binding.detailTenantTipeKamar.text = it.name
        }

        tenantViewModel.tenant.observe(viewLifecycleOwner) {
            binding.detailTenantNama.text = it.user.name
            binding.detailTenantPhone.text = it.user.phone
            binding.detailTenantTglMasuk.text = it.entryDate
            binding.detailTenantDue.text = it.dueDate
        }

        tenantViewModel.services.observe(viewLifecycleOwner) {
            it.forEach { service ->
                val chip = Chip(
                    context,
                    null,
                    R.style.Widget_MaterialComponents_Chip_Choice
                )
                chip.id = ViewCompat.generateViewId()
                chip.tag = "${service.id}"
                chip.text = service.name
                chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black))
                chip.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.primary)

                binding.detailTenantListService.addView(chip)
            }
        }
    }

    private fun fotoIdentitas() {
        if (!this::dialogFotoBinding.isInitialized) {
            dialogFotoBinding = DialogFotoBinding.inflate(layoutInflater, binding.root, false)
        }

        if (!this::dialogFoto.isInitialized) {
            dialogFotoBinding.fotoKtp.load("${VolleyClient.BASE_URL}/storage/${tenantViewModel.tenant.value!!.ktp}")
            dialogFoto = AlertDialog.Builder(requireContext())
                .setView(dialogFotoBinding.root)
                .show()
        } else {
            dialogFotoBinding.fotoKtp.load("${VolleyClient.BASE_URL}/storage/${tenantViewModel.tenant.value!!.ktp}")
            dialogFoto.show()
        }
    }

    private fun btnTambah() {
        if (!this::dialogAddTagihanBinding.isInitialized) {
            dialogAddTagihanBinding =
                DialogAddTagihanBinding.inflate(layoutInflater, binding.root, false)
        }

        if (!this::dialogAddTagihan.isInitialized) {
            dialogAddTagihan = AlertDialog.Builder(requireContext())
                .setView(dialogAddTagihanBinding.root)
                .setTitle("Tambah Tagihan")
                .setPositiveButton("Tambah", null)
                .setNegativeButton("Batal", null)
                .setCancelable(false)
                .create()

            dialogAddTagihan.setOnShowListener {
                dialogAddTagihan.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    addTagihan()
                }
            }

            dialogAddTagihan.show()
        } else {
            dialogAddTagihan.show()
        }
    }

    private fun addTagihan() {
        val nominal = dialogAddTagihanBinding.addTagihanNominal.text.toString().toInt()
        val deskripsi = dialogAddTagihanBinding.addTagihanDeskripsi.text.toString()

        val params = JSONObject()
        params.put("nominal", nominal)
        params.put("deskripsi", deskripsi)

        Log.d("ADD_TAGIHAN", params.toString())

        dialogAddTagihan.dismiss()
    }
}