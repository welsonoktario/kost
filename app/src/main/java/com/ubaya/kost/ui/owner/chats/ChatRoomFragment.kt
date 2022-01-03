package com.ubaya.kost.ui.owner.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubaya.kost.databinding.FragmentChatRoomBinding

class ChatRoomFragment : Fragment() {
    private var _binding: FragmentChatRoomBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)

        return _binding!!.root
    }
}