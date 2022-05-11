package com.ubaya.kost.ui.owner.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.ChatRoom
import com.ubaya.kost.databinding.CardChatRoomBinding

class ChatRoomAdapter(
    private val data: ArrayList<ChatRoom>,
    private val listener: CardChatRoomListener
) :
    RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    interface CardChatRoomListener {
        fun onCardClicked(position: Int)
    }

    inner class ChatRoomViewHolder(private val binding: CardChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardChatRoom.setOnClickListener(this)
        }

        fun bind(chatRoom: ChatRoom) {
            binding.cardChatRoomMsg.text = chatRoom.messages[chatRoom.messages.lastIndex].message
            binding.cardChatRoomTenant.text =
                "${chatRoom.tenant.user.name} ${chatRoom.tenant.room!!.noKamar}"
        }

        override fun onClick(v: View?) {
            listener.onCardClicked(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val binding =
            CardChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}