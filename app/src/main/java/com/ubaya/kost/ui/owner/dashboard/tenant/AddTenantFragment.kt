package com.ubaya.kost.ui.owner.dashboard.tenant

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import coil.load
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.ubaya.kost.R
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Room
import com.ubaya.kost.databinding.FragmentAddTenantBinding
import com.ubaya.kost.ui.owner.dashboard.DashboardViewModel
import com.ubaya.kost.util.ImageUtil
import com.ubaya.kost.util.VolleyClient
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddTenantFragment : Fragment() {
    private lateinit var ktpFile: File
    private lateinit var ktpUri: Uri
    private lateinit var durasi: Map<Int, String>
    private var _binding: FragmentAddTenantBinding? = null
    private var selectedDurasi = 0

    private val binding get() = _binding!!
    private val args: AddTenantFragmentArgs by navArgs()
    private val dashboardViewModel by navGraphViewModels<DashboardViewModel>(R.id.mobile_navigation)

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                ktpUri = it
                binding.addTenantImgKtp.load(it)
                binding.addTenantImgKtp.elevation = 1.0F

                val layoutParams = binding.addTenantCardFoto.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

                binding.addTenantCardFoto.layoutParams = layoutParams
            }
        }

    private val cameraResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) {
            if (it) {
                binding.addTenantImgKtp.load(ktpUri)
                binding.addTenantImgKtp.elevation = 1.0F

                val layoutParams = binding.addTenantCardFoto.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

                binding.addTenantCardFoto.layoutParams = layoutParams
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTenantBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addTenantCardFoto.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilih foto")
                .setItems(arrayOf("Galeri", "Kamera")) { _, which ->
                    when (which) {
                        0 -> galleryResultLauncher.launch("image/*")
                        1 -> openCamera()
                    }
                }
                .show()
        }

        binding.addTenantBtnTambah.setOnClickListener {
            if (this::ktpUri.isInitialized) {
                addTenant(prepareParams())
            }
        }

        binding.addTenantInputTglMasuk.setOnClickListener {
            openDatePicker()
        }

        durasi = mapOf(1 to "1 Bulan", 3 to "3 Bulan", 6 to "6 Bulan", 12 to "1 Tahun")

        val durasiText = durasi.map { it.value }.toList()

        binding.addTenantInputDurasi.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                durasiText
            )
        )

        binding.addTenantInputDurasi.setSelection(0)
        binding.addTenantInputDurasi.setText(durasiText[0], false)
        selectedDurasi = durasi.keys.toList()[0]
        binding.addTenantInputDurasi.setOnItemClickListener { _, _, position, _ ->
            selectedDurasi = durasi.keys.toList()[position]
        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                100
            )

            lifecycleScope.launchWhenStarted {
                getTmpFileUri().let {
                    ktpUri = it
                    cameraResultLauncher.launch(it)
                }
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        ktpFile = File.createTempFile("ktp_", ".jpeg").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), requireContext().packageName, ktpFile)
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

            binding.addTenantInputTglMasuk.setText(formattedDate)
        }
    }

    private fun prepareParams(): JSONObject {
        val user = JSONObject()
        user.put("username", binding.addTenantInputUserUsername.text.toString())
        user.put("name", binding.addTenantInputNameUser.text.toString())
        user.put("phone", binding.addTenantInputPhoneUser.text.toString())

        val params = JSONObject()
        params.put("room", args.room)
        params.put("ktp", ImageUtil().contentUriToBase64(activity?.contentResolver!!, ktpUri))
        params.put("entry_date", binding.addTenantInputTglMasuk.text.toString())
        params.put("user", user)
        params.put("durasi", selectedDurasi)

        return params
    }

    private fun addTenant(params: JSONObject) {
        binding.addTenantLoading.visibility = View.VISIBLE
        binding.addTenantBtnTambah.isEnabled = false
        val url = VolleyClient.API_URL + "/tenants"

        val request = object : JsonObjectRequest(
            Method.POST, url, params,
            { res ->
                binding.addTenantLoading.visibility = View.GONE
                val data = res.getJSONObject("data")

                try {
                    val index =
                        dashboardViewModel.rooms.value!!.indexOf(
                            dashboardViewModel.rooms.value!!.find { room -> room.id == args.room }
                        )
                    dashboardViewModel.rooms.value!![index] =
                        Gson().fromJson(data.toString(), Room::class.java)

                    findNavController().navigateUp()
                } catch (e: Exception) {
                    Log.e("ERROR_RESPONSE", e.message.toString())
                }
            },
            { err ->
                try {
                    binding.addTenantBtnTambah.isEnabled = true
                    val data = JSONObject(String(err.networkResponse.data))
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(data.getString("msg"))
                        .setNegativeButton("OK", null)
                        .show()
                } catch (e: Exception) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Terjadi kesalahan sistem")
                        .setPositiveButton("OK", null)
                        .show()
                }
                binding.addTenantLoading.visibility = View.GONE
            }
        ) {
            override fun getHeaders() = hashMapOf(
                "Authorization" to "Bearer ${Global.authToken}"
            )
        }

        VolleyClient.getInstance(requireContext()).addToRequestQueue(request)
    }
}