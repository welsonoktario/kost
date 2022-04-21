package com.ubaya.kost.ui.owner.komplain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Complain
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.databinding.FragmentKomplainBinding

class KomplainFragment : Fragment(), KomplainAdapter.ComplainListener {
    private lateinit var complains: ArrayList<Complain>
    private lateinit var adapter: KomplainAdapter

    private var _binding: FragmentKomplainBinding? = null

    private val binding get() = _binding!!
    private val komplainViewModel by navGraphViewModels<KomplainViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        komplainViewModel.loadComplains()
        _binding = FragmentKomplainBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCardComplainClick(position: Int) {
        if (complains[position].status == "pending") {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Apakah anda yakin ingin memproses komplain ini?")
                .setPositiveButton("Terima") { _, _ ->
                    komplainViewModel.updateComplain(
                        position,
                        "diproses"
                    )
                }
                .setNegativeButton("Tolak") { _, _ ->
                    komplainViewModel.updateComplain(
                        position,
                        "ditolak"
                    )
                }
                .setNeutralButton("Batal", null)
                .show()
        }
    }

    private fun initView() {
        complains = arrayListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = KomplainAdapter(complains, this)

        binding.komplainRV.adapter = adapter
        binding.komplainRV.layoutManager = layoutManager
    }

    private fun initObserver() {
        komplainViewModel.complains.observe(viewLifecycleOwner) {
            Log.d("Complains", it.toString())
            complains.clear()
            complains.addAll(it)
            adapter.notifyDataSetChanged()
        }

        komplainViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.komplainRV.visibility = View.GONE
                binding.komplainLoading.visibility = View.VISIBLE
            } else {
                binding.komplainRV.visibility = View.VISIBLE
                binding.komplainLoading.visibility = View.GONE
            }
        }

        komplainViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(komplainViewModel.error.value!!.msg)
                    .show()
                komplainViewModel.error.value = Error()
            }
        }
    }
}