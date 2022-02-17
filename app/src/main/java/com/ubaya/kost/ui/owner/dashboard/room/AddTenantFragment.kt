package com.ubaya.kost.ui.owner.dashboard.room

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.BuildConfig
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Service
import com.ubaya.kost.databinding.FragmentAddTenantBinding
import com.ubaya.kost.ui.owner.dashboard.DashboardViewModel
import com.ubaya.kost.util.observeOnce
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddTenantFragment : Fragment() {
    private lateinit var ktpFile: File
    private lateinit var ktpUri: Uri
    private var _binding: FragmentAddTenantBinding? = null

    private val binding get() = _binding!!
    private val args: AddTenantFragmentArgs by navArgs()
    private val dashboardViewModel by navGraphViewModels<DashboardViewModel>(R.id.mobile_navigation)
    private val roomViewModel by navGraphViewModels<RoomViewModel>(R.id.mobile_navigation)

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

        if (roomViewModel.services.value == null) {
            roomViewModel.loadServices(dashboardViewModel.kost.value!!)
        }

        initObserver()

        binding.addTenantCardFoto.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilih foto")
                .setItems(arrayOf("Galeri", "Kamera")) { dialog, which ->
                    when (which) {
                        0 -> galleryResultLauncher.launch("image/*")
                        1 -> openCamera()
                    }
                }
                .show()
        }

        binding.addTenantBtnTambah.setOnClickListener {
            if (this::ktpUri.isInitialized) {
                roomViewModel.addTenant(prepareParams())

//                Toast.makeText(context, params.toString(), Toast.LENGTH_LONG).show()
            }
        }

        binding.addTenantInputTglMasuk.setOnClickListener {
            openDatePicker()
        }
    }

    private fun loadServiceChip(services: ArrayList<Service>) {
        services.forEach {
            val chip = Chip(
                context,
                null,
                R.style.Widget_MaterialComponents_Chip_Filter
            )
            chip.id = ViewCompat.generateViewId()
            chip.tag = "${it.id}"
            chip.text = it.name
            chip.isClickable = true
            chip.isCheckable = true

            binding.addTenantChipGroupServices.addView(chip)
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

        return FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, ktpFile)
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
        user.put("password", binding.addTenantInputPassUser.text.toString())
        user.put("phone", binding.addTenantInputPhoneUser.text.toString())

        val services = JSONArray()
        var i = 0

        while (i < binding.addTenantChipGroupServices.childCount) {
            val chip = binding.addTenantChipGroupServices.getChildAt(i) as Chip

            if (chip.isChecked) {
                services.put(chip.tag.toString().toInt())
            }
            i++
        }

        val params = JSONObject()
        params.put("room", args.room)
//        params.put("ktp", ImageUtil().contentUriToBase64(activity?.contentResolver!!, ktpUri))
        params.put("entry_date", binding.addTenantInputTglMasuk.text.toString())
        params.put("user", user)
        params.put("services", services)

        return params
    }

    private fun initObserver() {
        roomViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.pbAddTenantLoading.visibility = if (it) View.VISIBLE else View.GONE
        }

        roomViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(roomViewModel.error.value!!.msg)
                    .setPositiveButton("Coba Lagi") { _, _ ->
                        roomViewModel.addTenant(prepareParams())
                    }.show()
            }
        }

        roomViewModel.room.observeOnce(viewLifecycleOwner) {
            val index =
                dashboardViewModel.rooms.value!!.indexOf(
                    dashboardViewModel.rooms.value!!.find { room -> room.id == args.room }
                )
            dashboardViewModel.rooms.value!![index] = it

            findNavController().navigateUp()
        }

        roomViewModel.services.observeOnce(viewLifecycleOwner) {
            loadServiceChip(it)
        }
    }
}