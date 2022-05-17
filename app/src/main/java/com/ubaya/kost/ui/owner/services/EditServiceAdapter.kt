package com.ubaya.kost.ui.owner.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Service
import com.ubaya.kost.databinding.CardServiceBinding
import com.ubaya.kost.util.NumberUtil

class EditServiceAdapter(
    private val data: ArrayList<Service>,
    private val listener: EditServiceListener
) :
    RecyclerView.Adapter<EditServiceAdapter.EditServiceViewHolder>() {

    interface EditServiceListener {
        fun onCardEditServiceClick(position: Int)
    }

    inner class EditServiceViewHolder(private val binding: CardServiceBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardService.setOnClickListener(this)
        }

        fun bind(service: Service) {
            binding.cardServiceName.text = service.name
            binding.cardServiceCost.text = NumberUtil().rupiah(service.cost!!)
        }

        override fun onClick(v: View?) {
            listener.onCardEditServiceClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditServiceViewHolder {
        val binding = CardServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return EditServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditServiceViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}