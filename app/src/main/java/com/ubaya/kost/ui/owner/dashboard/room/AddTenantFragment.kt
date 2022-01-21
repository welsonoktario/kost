package com.ubaya.kost.ui.owner.dashboard.room

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.BuildConfig
import com.ubaya.kost.data.models.Kost
import com.ubaya.kost.databinding.FragmentAddTenantBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

private const val ARG_ROOM = "ROOM"

class AddTenantFragment : Fragment() {
    private lateinit var ktpFile: File
    private lateinit var ktpUri: Uri
    private var _binding: FragmentAddTenantBinding? = null
    private var kost: Kost? = null

    private val binding get() = _binding!!

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            kost = it.getParcelable(ARG_ROOM)
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
                .setItems(arrayOf("Galeri", "Kamera")) { dialog, which ->
                    when (which) {
                        0 -> galleryResultLauncher.launch("image/*")
                        1 -> openCamera()
                    }
                }
                .show()
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
}