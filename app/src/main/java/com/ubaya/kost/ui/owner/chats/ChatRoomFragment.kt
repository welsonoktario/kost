package com.ubaya.kost.ui.owner.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.ubaya.kost.data.models.Message
import com.ubaya.kost.databinding.FragmentChatRoomBinding

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
        chatViewModel.loadMessages(args.chatRoom)
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    private fun initView() {
        binding
    }

    private fun initObserver() {

    }
}