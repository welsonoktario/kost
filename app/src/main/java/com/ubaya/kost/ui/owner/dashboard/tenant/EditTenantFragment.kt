package com.ubaya.kost.ui.owner.dashboard.tenant

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.databinding.FragmentEditTenantBinding
import com.ubaya.kost.util.VolleyClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class EditTenantFragment : Fragment() {
    private lateinit var tenant: Tenant

    private var _binding: FragmentEditTenantBinding? = null

    private val binding get() = _binding!!
    private val tenantViewModel by navGraphViewModels<TenantViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTenantBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() {
        tenant = tenantViewModel.tenant.value!!

        binding.editTenantUserUsername.setText(tenant.user.username)
        binding.editTenantUserName.setText(tenant.user.name)
        binding.editTenantUserPhone.setText(tenant.user.phone)

        binding.editBiodataBtnSimpan.setOnClickListener {
            val username = binding.editTenantUserUsername.text.toString()
            val name = binding.editTenantUserName.text.toString()
            val phone = binding.editTenantUserPhone.text.toString()
            editTenant(username, name, phone)
        }
    }

    private fun editTenant(username: String, name: String, phone: String) {
        val url = "${VolleyClient.API_URL}/tenants/${tenant.id}"
        val params = JSONObject()
        params.put("username", username)
        params.put("name", name)
        params.put("phone", phone)

        lifecycleScope.launch {
            val request = object : JsonObjectRequest(
                Method.PUT,
                url,
                params,
                { res ->
                    val newUser = tenant.user.copy(
                        username = username,
                        name = name,
                        phone = phone
                    )
                    tenant.user = newUser
                    tenantViewModel.setTenant(tenant)
                    findNavController().navigateUp()
                },
                { err ->
                    Log.e("ERR", err.message.toString())
                    try {
                        val data = JSONObject(String(err.networkResponse.data))

                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Error")
                            .setMessage(data.getString("msg"))
                            .setPositiveButton("Tutup", null)
                            .show()
                    } catch (e: Exception) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Error")
                            .setMessage("Terjadi kesalahan server")
                            .setPositiveButton("Tutup", null)
                            .show()
                    }
                }
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
        }
    }
}