package com.ubaya.kost.ui.owner.dashboard

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Room
import com.ubaya.kost.data.models.RoomType
import com.ubaya.kost.databinding.FragmentDashboardBinding
import com.ubaya.kost.util.observeOnce

class DashboardFragment : Fragment(), RoomAdapter.RoomListener {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var roomTypes: List<String>
    private lateinit var rooms: ArrayList<Room>
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var gridLayutManager: GridLayoutManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val dashboardViewModel by navGraphViewModels<DashboardViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        if (dashboardViewModel.kost.value == null && dashboardViewModel.roomTypes.value == null) {
            dashboardViewModel.loadData()
        }
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        Log.d("DASHBOARD", "onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.owner_dashboard_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_catatan -> findNavController().navigate(R.id.action_fragment_dashboard_to_fragment_catatan)
            R.id.menu_chats -> findNavController().navigate(R.id.action_fragment_dashboard_to_fragment_chats)
            R.id.menu_notifications -> findNavController().navigate(R.id.action_fragment_dashboard_to_fragment_notifications)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initObserver() {
        dashboardViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        dashboardViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(dashboardViewModel.error.value!!.msg)
                    .setPositiveButton("Coba Lagi") { _, _ ->
                        dashboardViewModel.loadData()
                    }.show()
            }
        }

        dashboardViewModel.roomTypes.observeOnce(viewLifecycleOwner) {
            initDropdown(it)
        }

        dashboardViewModel.selectedRoomType.observe(viewLifecycleOwner) {
            dashboardViewModel.setRooms(it)
        }
    }

    private fun initView() {
        rooms = arrayListOf()
        roomAdapter = RoomAdapter(rooms, this)
        gridLayutManager = GridLayoutManager(requireContext(), 2)
        binding.dashboardRoomRV.apply {
            adapter = roomAdapter
            layoutManager = gridLayutManager
        }

        dashboardViewModel.rooms.observe(viewLifecycleOwner) {
            roomAdapter.notifyItemRangeRemoved(0, rooms.size)
            rooms.clear()
            rooms.addAll(it)
            roomAdapter.notifyItemRangeInserted(0, rooms.size)
        }
    }

    private fun initDropdown(types: ArrayList<RoomType>) {
        roomTypes = types.map { type -> type.name!! }
        binding.dashboardDropdownRoomType.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    roomTypes
                )
            )
            setSelection(0)
            setText(roomTypes[0], false)
        }

        binding.dashboardDropdownRoomType.setOnItemClickListener { parent, view, position, id ->
            dashboardViewModel.selectedRoomType.value = types[position]
        }
    }

    override fun onCardClick(position: Int) {
        val room = rooms[position]
        val action =
            if (room.tenant != null) {
                DashboardFragmentDirections.actionFragmentDashboardToFragmentDetailTenant(
                    room.id
                )
            } else {
                DashboardFragmentDirections.actionFragmentDashboardToFragmentAddTenant(room.id)
            }

        findNavController().navigate(action)
    }
}