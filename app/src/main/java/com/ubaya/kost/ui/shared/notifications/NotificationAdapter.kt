package com.ubaya.kost.ui.shared.notifications

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Notification
import com.ubaya.kost.databinding.CardNotificationBinding

class NotificationAdapter(
    private val data: ArrayList<Notification>,
    private val listener: NotificationListener
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    interface NotificationListener {
        fun onCardNotifClick(position: Int)
    }

    inner class NotificationViewHolder(val binding: CardNotificationBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardNotifLayout.setOnClickListener(this)
        }

        fun bind(notification: Notification) {
            binding.cardNotifMsg.text = notification.message
            binding.cardNotifTanggal.text = notification.date

            if (!notification.isRead) {
                binding.cardNotifMsg.typeface = Typeface.DEFAULT_BOLD
            }
        }

        override fun onClick(v: View?) {
            listener.onCardNotifClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = CardNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}