package com.ubaya.kost.ui.shared.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Notification
import com.ubaya.kost.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment(), NotificationAdapter.NotificationListener {

    private lateinit var notifications: ArrayList<Notification>
    private lateinit var adapter: NotificationAdapter

    private var _binding: FragmentNotificationsBinding? = null

    private val binding get() = _binding!!
    private val notificationsViewModel by navGraphViewModels<NotificationsViewModel>(R.id.mobile_navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel.loadNotifications()
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    private fun initView() {
        notifications = arrayListOf()
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationAdapter(notifications, this)

        binding.notifiationsRV.adapter = adapter
        binding.notifiationsRV.layoutManager = layoutManager
    }

    private fun initObserver() {
        notificationsViewModel.notifications.observe(viewLifecycleOwner) {
            notifications.clear()
            notifications.addAll(it)
            adapter.notifyDataSetChanged()
        }

        notificationsViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.notifiationsRV.visibility = View.GONE
                binding.notificationsLoading.visibility = View.VISIBLE
            } else {
                binding.notifiationsRV.visibility = View.VISIBLE
                binding.notificationsLoading.visibility = View.GONE
            }
        }

        notificationsViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(notificationsViewModel.error.value!!.msg)
                    .show()
                notificationsViewModel.error.value = Error()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCardNotifClick(position: Int) {
        notificationsViewModel.readNotification(position)
    }
}