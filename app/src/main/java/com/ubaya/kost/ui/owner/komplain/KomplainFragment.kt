package com.ubaya.kost.ui.owner.komplain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubaya.kost.databinding.FragmentKomplainBinding

class KomplainFragment : Fragment() {
    private var _binding: FragmentKomplainBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKomplainBinding.inflate(inflater, container, false)
        return _binding!!.root
    }
}