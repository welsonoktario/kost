package com.ubaya.kost.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.ubaya.kost.data.models.Kost
import com.ubaya.kost.data.models.RoomType
import com.ubaya.kost.data.models.Service
import com.ubaya.kost.data.models.User
import com.ubaya.kost.databinding.FragmentRegisterBinding

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
            val roomType = RoomType()
            roomTypes.add(roomType)
            roomTypeAdapter.notifyItemInserted(roomTypes.indexOf(roomType))
        }

        binding.registerBtnService.setOnClickListener {
            val service = Service()
            services.add(service)
            serviceAdapter.notifyItemInserted(services.indexOf(service))
        }

        binding.registerBtnDaftar.setOnClickListener {
            if (checkForm()) {
                val user = setUser()
                kost = Kost(
                    0,
                    binding.registerInputNamaKost.text.toString(),
                    binding.registerInputAlamatKost.text.toString(),
                    user
                )
                val gson = GsonBuilder().create()
                val params: Map<String, String> = hashMapOf(
                    "user" to gson.toJson(user),
                    "kost" to gson.toJson(kost),
                    "types" to gson.toJson(roomTypes),
                    "services" to gson.toJson(services)
                )

                Toast.makeText(requireContext(), gson.toJson(params), Toast.LENGTH_SHORT).show()
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
            binding.registerInputAlamatKost
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

        return isValid
    }

    private fun setUser(): User {
        val username = binding.registerInpuUsernameUser.text.toString()
        val name = binding.registerInputNamaUser.text.toString()
        val phone = binding.registerInputHPUser.text.toString()

        return User(username, name, phone, "Owner")
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