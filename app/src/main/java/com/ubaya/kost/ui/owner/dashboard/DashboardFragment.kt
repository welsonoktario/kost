package com.ubaya.kost.ui.owner.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.MainActivity
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Room
import com.ubaya.kost.data.models.RoomType
import com.ubaya.kost.databinding.FragmentDashboardBinding
import com.ubaya.kost.ui.shared.notifications.NotificationsViewModel
import com.ubaya.kost.util.PrefManager
import com.ubaya.kost.util.observeOnce

class DashboardFragment : Fragment(), RoomAdapter.RoomListener {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var roomTypes: List<String>
    private lateinit var rooms: ArrayList<Room>
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var gridLayutManager: GridLayoutManager
    private lateinit var notifCountBadge: BadgeDrawable

    private val binding get() = _binding!!
    private val dashboardViewModel by navGraphViewModels<DashboardViewModel>(R.id.mobile_navigation)
    private val notificationsViewModel by navGraphViewModels<NotificationsViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.owner_dashboard_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_catatan -> findNavController().navigate(R.id.action_fragment_dashboard_to_fragment_catatan)
                    R.id.menu_komplain -> findNavController().navigate(R.id.action_fragment_dashboard_to_fragment_komplain)
                    R.id.menu_message -> findNavController().navigate(R.id.action_fragment_dashboard_to_fragment_chats)
                    R.id.menu_notifications -> findNavController().navigate(R.id.action_fragment_dashboard_to_fragment_notifications)
                    R.id.menu_logout -> logout()
                }

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        if (dashboardViewModel.kost.value == null && dashboardViewModel.roomTypes.value == null) {
            dashboardViewModel.loadData()
        }

        if (notificationsViewModel.notifications.value!!.isEmpty()) {
            notificationsViewModel.loadNotifications()
        }

        initView()
        initObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("UnsafeOptInUsageError")
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

        dashboardViewModel.rooms.observe(viewLifecycleOwner) {
            dashboardViewModel.isLoading.value = true
            rooms.clear()
            rooms.addAll(it)
            roomAdapter.notifyDataSetChanged()
            dashboardViewModel.isLoading.value = false
        }

        notificationsViewModel.notifications.observe(viewLifecycleOwner) {
            val toolbar = (requireActivity() as MainActivity).findViewById<Toolbar>(R.id.toolbar)
            val count = it.filter { notif -> !notif.isRead }.size

            if (count > 0) {
                notifCountBadge.number = count
                BadgeUtils.attachBadgeDrawable(notifCountBadge, toolbar, R.id.menu_notifications)
            } else {
                BadgeUtils.detachBadgeDrawable(notifCountBadge, toolbar, R.id.menu_notifications)
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initView() {
        rooms = arrayListOf()
        roomAdapter = RoomAdapter(rooms, this)
        gridLayutManager = GridLayoutManager(requireContext(), 2)
        binding.dashboardRoomRV.apply {
            adapter = roomAdapter
            layoutManager = gridLayutManager
        }
        notifCountBadge = BadgeDrawable.create(requireContext())
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
                DashboardFragmentDirections.actionFragmentDashboardToFragmentDetailTenant(room.id)
            } else {
                DashboardFragmentDirections.actionFragmentDashboardToFragmentAddTenant(room.id)
            }

        findNavController().navigate(action)
    }

    private fun logout() {
        val prefs = PrefManager.getInstance(requireContext())
        prefs.clear()

        val navOptions = NavOptions
            .Builder()
            .setPopUpTo(R.id.fragment_dashboard, true)
            .build()
        findNavController().navigate(R.id.fragment_login, null, navOptions)
        requireActivity().recreate()
    }
}