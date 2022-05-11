package com.ubaya.kost.ui.owner.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.data.models.ChatRoom
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.databinding.FragmentChatsBinding

class ChatsFragment : Fragment(), ChatRoomAdapter.CardChatRoomListener {
    private lateinit var chatRooms: ArrayList<ChatRoom>
    private lateinit var adapter: ChatRoomAdapter

    private var _binding: FragmentChatsBinding? = null

    private val binding get() = _binding!!
    private val chatViewModel by viewModels<ChatViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatViewModel.loadChatRooms()
        _binding = FragmentChatsBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        initView()
    }

    private fun initView() {
        chatRooms = chatViewModel.chatRooms.value!!
        binding.chatRV.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObserver() {
        chatViewModel.chatRooms.observe(viewLifecycleOwner) {
            chatRooms.clear()
            chatRooms.addAll(it)

            adapter.notifyDataSetChanged()

            binding.chatEmpty.visibility =
                if (!chatViewModel.isLoading.value!! && chatRooms.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        chatViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.chatRV.visibility = View.GONE
                binding.chatLoading.visibility = View.VISIBLE
            } else {
                binding.chatRV.visibility = View.VISIBLE
                binding.chatLoading.visibility = View.GONE
            }
        }

        chatViewModel.error.observe(viewLifecycleOwner) {
            if (it.isError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(chatViewModel.error.value!!.msg)
                    .show()
                chatViewModel.error.value = Error()
            }
        }
    }

    override fun onCardClicked(position: Int) {
        val id = chatRooms[position].id
        val action = ChatsFragmentDirections.actionFragmentChatsToFragmentChatRoom(id)
        findNavController().navigate(action)
    }
}