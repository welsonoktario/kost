package com.ubaya.kost.ui.owner.catatan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubaya.kost.databinding.FragmentCatatanBinding

class CatatanFragment : Fragment() {
    private var _binding: FragmentCatatanBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatatanBinding.inflate(inflater, container, false)

        return _binding!!.root
    }
}