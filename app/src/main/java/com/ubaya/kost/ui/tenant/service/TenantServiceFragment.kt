package com.ubaya.kost.ui.tenant.service

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request.Method.POST
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Service
import com.ubaya.kost.databinding.FragmentTenantServiceBinding
import com.ubaya.kost.util.NumberUtil
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.VolleyClient.Companion.API_URL
import com.ubaya.kost.util.fromJson
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TenantServiceFragment : Fragment() {
    private var _binding: FragmentTenantServiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var services: ArrayList<Service>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTenantServiceBinding.inflate(layoutInflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadService()

        binding.tenantServiceBtnTambah.setOnClickListener {
            tambahService()
        }

        binding.tenantServiceBtnTanggal.setOnClickListener {
            openDatePicker()
        }
    }

    private fun loadService() {
        val url = "${API_URL}/services/${Global.authKost.id}"
        val request = object : JsonObjectRequest(url,
            { res ->
                try {
                    val data = res.getString("data")
                    services = Gson().fromJson(data)
                    initList()
                } catch (e: Exception) {
                    Log.e("ERR", e.toString())
                }
            },
            {
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    "Terjadi kesalahan memuat service",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Muat Ulang") { loadService() }
                    .show()
            }
        ) {
            override fun getHeaders() = hashMapOf(
                "Authorization" to "Bearer ${Global.authToken}"
            )
        }

        VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
    }

    private fun initList() {
        services.forEach {
            val row = LinearLayout(requireContext())
            row.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            row.orientation = LinearLayout.HORIZONTAL

            val service = CheckBox(requireContext())
            service.tag = it.id
            service.text = it.name

            val price = TextView(requireContext())
            price.text = NumberUtil().rupiah(it.cost!!)

            row.addView(
                service,
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            )
            row.addView(price)

            binding.tenantServiceLayout.addView(row)
        }
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Tanggal masuk")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(childFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener {
            val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
            calendar.timeInMillis = it
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = format.format(calendar.time)

            binding.tenantServiceBtnTanggal.setText(formattedDate)
        }
    }

    private fun getSelected(): List<Int> {
        val list = binding.tenantServiceLayout
        val selected = mutableListOf<Int>()

        for (i in 0 until list.childCount) {
            val row = list.getChildAt(i) as LinearLayout
            val checkBox = row.getChildAt(0) as CheckBox

            if (checkBox.isChecked) {
                selected.add(checkBox.tag.toString().toInt())
            }
        }

        return selected.toList()
    }

    private fun tambahService() {
        val params = JSONObject()
        params.put("services", JSONArray(getSelected()))
        params.put("tenant", Global.authTenant.id)
        params.put("tanggal", binding.tenantServiceBtnTanggal.text.toString())

        val url = "${API_URL}/tenant-service"
        val request = object : JsonObjectRequest(POST, url, params,
            { res ->
                findNavController().navigateUp()
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    res.getString("msg"),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            },
            {
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    "Terjadi kesalahan mengajukan service",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Muat Ulang") { loadService() }
                    .show()
            }
        ) {
            override fun getHeaders() = hashMapOf(
                "Authorization" to "Bearer ${Global.authToken}"
            )
        }

        VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
    }
}