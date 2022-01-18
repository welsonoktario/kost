package com.ubaya.kost.ui.owner.dashboard.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.ubaya.kost.data.models.Kost
import com.ubaya.kost.databinding.FragmentAddTenantBinding

private const val ARG_KOST = "KOST"

class AddTenantFragment : Fragment() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            kost = it.getParcelable(ARG_KOST)
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

        binding.addTenantCardFoto.setOnClickListener { galleryResultLauncher.launch("image/*") }
    }

    companion object {
        @JvmStatic
        fun newInstance(kost: Kost) =
            AddTenantFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_KOST, kost)
                }
            }
    }
}