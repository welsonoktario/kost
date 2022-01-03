package com.ubaya.kost.ui.owner.pembukuan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubaya.kost.databinding.FragmentPembukuanBinding

class PembukuanFragment : Fragment() {
    private var _binding: FragmentPembukuanBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembukuanBinding.inflate(inflater, container, false)
        return _binding!!.root
    }
}