package com.ubaya.kost.ui.owner.pembukuan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubaya.kost.data.models.Pengeluaran
import com.ubaya.kost.databinding.FragmentPengeluaranBinding

class PengeluaranFragment : Fragment() {

    private lateinit var pengeluarans: ArrayList<Pengeluaran>
    private lateinit var adapter: PengeluaranAdapter

    private var _binding: FragmentPengeluaranBinding? = null

    private val binding get() = _binding!!
    private val pembukuanViewModel: PembukuanViewModel by viewModels(
        ownerProducer = {
            requireParentFragment()
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengeluaranBinding.inflate(layoutInflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        initView()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        pengeluarans = arrayListOf()
        adapter = PengeluaranAdapter(pengeluarans)

        binding.pengeluaranRV.adapter = adapter
        binding.pengeluaranRV.layoutManager = layoutManager
    }

    private fun initObserver() {
        pembukuanViewModel.pengeluarans.observe(viewLifecycleOwner) {
            pengeluarans.clear()
            pengeluarans.addAll(it)

            adapter.notifyDataSetChanged()
        }
    }
}