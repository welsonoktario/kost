package com.ubaya.kost.ui.owner.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Room
import com.ubaya.kost.databinding.CardDashboardRoomBinding

class RoomAdapter(private val data: ArrayList<Room>, val listener: RoomListener) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    interface RoomListener {
        fun onCardClick(position: Int)
    }

    inner class RoomViewHolder(val binding: CardDashboardRoomBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardDashboardRoomLayout.setOnClickListener(this)
        }

        fun bind(room: Room) {
            binding.cardDashboardRoomID.text = room.noKamar.toString()

            if (room.tenant != null) {
                binding.cardDashboardRoomLayout.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.primary
                    )
                )
                binding.cardDashboardRoomID.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.design_default_color_on_primary
                    )
                )
            }
        }

        override fun onClick(v: View?) {
            listener.onCardClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding =
            CardDashboardRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}