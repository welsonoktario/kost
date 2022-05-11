package com.ubaya.kost.ui.shared.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Message
import com.ubaya.kost.databinding.FragmentChatRoomBinding
import com.ubaya.kost.ui.owner.chats.ChatRoomAdapter

class ChatRoomFragment : Fragment() {
    private lateinit var messages: ArrayList<Message>
    private lateinit var adapter: MessageAdapter

    private var _binding: FragmentChatRoomBinding? = null

    private val binding get() = _binding!!
    private val chatViewModel by viewModels<ChatViewModel>()
    private val args by navArgs<ChatRoomFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatViewModel.loadMessages(args.kost, args.tenant)

        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)

        return _binding!!.root
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

    private fun initView() {
        val user = Global.authUser
        messages = chatViewModel.messages.value!!
        adapter = MessageAdapter(messages, user.type)

        binding.chatRoomRV.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.chatRoomSend.setOnClickListener {
            val msg = binding.chatRoomChatInput.text
            val chatRoom = chatViewModel.chatRoom.value!!
            chatViewModel.addMessage(chatRoom.id, msg.toString())
            msg!!.clear()
        }
    }

    private fun initObserver() {
        chatViewModel.messages.observe(viewLifecycleOwner) {
            messages.clear()
            messages.addAll(it)

            adapter.notifyDataSetChanged()

            binding.chatRoomEmpty.visibility =
                if (!chatViewModel.isLoading.value!! && messages.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        chatViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.chatRoomRV.visibility = View.GONE
                binding.chatRoomLoading.visibility = View.VISIBLE
                binding.chatRoomSend.isEnabled = false
            } else {
                binding.chatRoomRV.visibility = View.VISIBLE
                binding.chatRoomLoading.visibility = View.GONE
                binding.chatRoomSend.isEnabled = true
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
}