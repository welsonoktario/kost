package com.ubaya.kost.ui.owner.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubaya.kost.R
import com.ubaya.kost.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {
    private var _binding: FragmentChatsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChatRoom.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_chats_to_navigation_chat_room)
        }
    }
}