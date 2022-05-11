package com.ubaya.kost.ui.owner.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Message
import com.ubaya.kost.databinding.CardMessageBinding

class MessageAdapter(private val data: ArrayList<Message>, private val type: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(private val binding: CardMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            if (message.isOwner && type == "Owner" || type == "Tenant") {
                binding.cardMessageIsEnd.visibility = View.VISIBLE
                binding.cardMessageUser.visibility = View.GONE
            } else if (!message.isOwner && type == "Owner") {
                binding.cardMessageIsEnd.visibility = View.GONE
                binding.cardMessageUser.visibility = View.VISIBLE
                binding.cardMessageUser.text = message.tenant!!.user.name
            }

            binding.cardMessageMessage.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = CardMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}