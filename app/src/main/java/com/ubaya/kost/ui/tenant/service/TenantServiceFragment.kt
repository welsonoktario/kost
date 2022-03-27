package com.ubaya.kost.ui.tenant.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubaya.kost.databinding.FragmentTenantServiceBinding

class TenantServiceFragment : Fragment() {
    private var _binding: FragmentTenantServiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTenantServiceBinding.inflate(layoutInflater, container, false)

        return _binding!!.root
    }


}