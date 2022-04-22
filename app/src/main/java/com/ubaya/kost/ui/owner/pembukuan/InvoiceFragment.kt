package com.ubaya.kost.ui.owner.pembukuan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.ubaya.kost.R
import com.ubaya.kost.databinding.FragmentInvoiceBinding
import com.ubaya.kost.databinding.FragmentPembukuanBinding

class InvoiceFragment : Fragment() {

    private lateinit var adapter: InvoiceAdapter

    private var _binding: FragmentInvoiceBinding? = null

    private val binding get() = _binding!!
    private val pembukuanViewModel by viewModels<PembukuanViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoiceBinding.inflate(layoutInflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    private fun initView() {

    }

    private fun initObserver() {

    }
}