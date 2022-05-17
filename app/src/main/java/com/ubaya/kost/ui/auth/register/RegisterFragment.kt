package com.ubaya.kost.ui.auth.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Kost
import com.ubaya.kost.data.models.RoomType
import com.ubaya.kost.data.models.Service
import com.ubaya.kost.data.models.User
import com.ubaya.kost.databinding.FragmentRegisterBinding
import com.ubaya.kost.util.PrefManager
import com.ubaya.kost.util.VolleyClient
import org.json.JSONArray
import org.json.JSONObject

class RegisterFragment : Fragment(), RoomTypeAdapter.CardJenisClickListener,
    ServiceAdapter.CardServiceClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private lateinit var roomTypeAdapter: RoomTypeAdapter
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var kost: Kost

    private val binding get() = _binding!!
    private val roomTypes = arrayListOf<RoomType>()
    private val services = arrayListOf<Service>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        roomTypeAdapter = RoomTypeAdapter(roomTypes, this)
        serviceAdapter = ServiceAdapter(services, this)

        binding.registerRVJenis.apply {
            adapter = roomTypeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.registerRVService.apply {
            adapter = serviceAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.registerBtnJenis.setOnClickListener {
            roomTypes.add(0, RoomType())
            roomTypeAdapter.notifyItemInserted(0)
        }

        binding.registerBtnService.setOnClickListener {
            services.add(0, Service())
            serviceAdapter.notifyItemInserted(0)
        }

        binding.registerBtnDaftar.setOnClickListener {
            if (checkForm()) {
                register()
            }
        }
    }

    private fun checkForm(): Boolean {
        var isValid = true
        val inputs = arrayListOf(
            binding.registerInpuUsernameUser,
            binding.registerInputNamaUser,
            binding.registerInputHPUser,
            binding.registerInputPasswordUser,
            binding.registerInputPasswordConfirmUser,
            binding.registerInputNamaKost,
            binding.registerInputAlamatKost,
            binding.registerInputNominal,
            binding.registerInputInterval
        )

        inputs.forEach { input ->
            if (input.text.isNullOrEmpty()) {
                input.error = "Field tidak boleh kosong"
                isValid = false
            } else {
                input.error = null
            }
        }

        if (binding.registerInputHPUser.text!!.isNotEmpty() && binding.registerInputHPUser.length() < 10) {
            isValid = false
            binding.registerInputHPUser.error = "Masukkan nomor yang valid"
        } else {
            binding.registerInputHPUser.error = null
        }

        if (binding.registerInputPasswordUser.text.toString() != binding.registerInputPasswordConfirmUser.text.toString()) {
            isValid = false
            binding.registerInputPasswordConfirmUser.error = "Password tidak cocok"
        }

        if (roomTypes.isEmpty()) {
            isValid = false
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Tambahkan minimal 1 jenis kamar")
                .setPositiveButton("OK", null)
                .show()
        }

        return isValid
    }

    private fun setUser(): User {
        val username = binding.registerInpuUsernameUser.text.toString()
        val name = binding.registerInputNamaUser.text.toString()
        val phone = binding.registerInputHPUser.text.toString()

        return User(username, name, phone, "Owner")
    }

    private fun register() {
        val pref = PrefManager.getInstance(requireContext())
        kost = Kost(
            name = binding.registerInputNamaKost.text.toString(),
            address = binding.registerInputAlamatKost.text.toString(),
            user = setUser(),
            nominalDenda = binding.registerInputNominal.text.toString().toInt(),
            intervalDenda = binding.registerInputInterval.text.toString().toInt(),
            dendaBerlaku = binding.registerInputDendaBerlaku.text.toString().toInt()
        )
        val gson = GsonBuilder().disableHtmlEscaping().create()

        val params2 = JSONObject()
        params2.put("kost", JSONObject(gson.toJson(kost)))
        params2.put("types", JSONArray(gson.toJson(roomTypes)))
        params2.put("services", JSONArray(gson.toJson(services)))
        params2.put("password", binding.registerInputPasswordUser.text.toString())

        val url = VolleyClient.API_URL + "/auth/register"
        val request = JsonObjectRequest(Request.Method.POST, url, params2,
            { res ->
                val data = JSONObject(res["data"].toString())
                val dataUser = data.getJSONObject("user")
                val user = Gson().fromJson(dataUser.toString(), User::class.java)
                val token = data["token"].toString()

                val kosts = dataUser.getJSONArray("kost")
                val kost = Gson().fromJson(kosts[0].toString(), Kost::class.java)

                pref.apply {
                    authUser = user
                    authToken = token
                    authKost = kost
                }

                Global.apply {
                    authUser = user
                    authToken = token
                    authKost = kost
                }

                findNavController().navigate(R.id.action_navigation_register_to_owner_navigation)
            },
            { err ->
                Log.d("error", err.toString())
            }
        )

        VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
    }

    override fun onRemoveJenisClick(position: Int) {
        roomTypes.removeAt(position)
        roomTypeAdapter.notifyItemRemoved(position)
    }

    override fun onNamaJenisChanged(position: Int, newValue: String) {
        roomTypes[position].name = newValue
    }

    override fun onHargaJenisChanged(position: Int, newValue: Int) {
        roomTypes[position].cost = newValue
    }

    override fun onJumlahJenisChanged(position: Int, newValue: Int) {
        roomTypes[position].count = newValue
    }

    override fun onRemoveServiceClick(position: Int) {
        services.removeAt(position)
        serviceAdapter.notifyItemRemoved(position)
    }

    override fun onNamaServiceChanged(position: Int, newValue: String) {
        services[position].name = newValue
    }

    override fun onDeskripsiServiceChanged(position: Int, newValue: String) {
        services[position].description = newValue
    }

    override fun onHargaServiceChanged(position: Int, newValue: Int) {
        services[position].cost = newValue
    }
}