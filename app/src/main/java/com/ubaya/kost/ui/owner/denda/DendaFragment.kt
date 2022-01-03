package com.ubaya.kost.ui.owner.denda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubaya.kost.databinding.FragmentDendaBinding

class DendaFragment : Fragment() {
    private var _binding: FragmentDendaBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDendaBinding.inflate(inflater, container, false)
        return _binding!!.root
    }
}