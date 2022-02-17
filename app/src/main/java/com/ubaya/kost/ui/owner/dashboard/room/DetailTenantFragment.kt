package com.ubaya.kost.ui.owner.dashboard.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.ubaya.kost.R
import com.ubaya.kost.databinding.FragmentDetailTenantBinding

class DetailTenantFragment : Fragment() {
    private var _binding: FragmentDetailTenantBinding? = null

    private val binding get() = _binding!!
    private val args: DetailTenantFragmentArgs by navArgs()
    private val roomViewModel: RoomViewModel by navGraphViewModels(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTenantBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomViewModel.loadRoom(args.room)
        initObserver()
    }

    private fun initObserver() {
        roomViewModel.roomType.observe(viewLifecycleOwner) {
            binding.detailTenantTipeKamar.text = it.name
        }

        roomViewModel.tenant.observe(viewLifecycleOwner) {
            binding.detailTenantNama.text = it.user.name
            binding.detailTenantPhone.text = it.user.phone
            binding.detailTenantTglMasuk.text = it.entryDate
            binding.detailTenantDue.text = it.dueDate
        }

        roomViewModel.services.observe(viewLifecycleOwner) {
            val names = it.map { service -> service.name }
            binding.detailTenantListService.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
        }
    }
}