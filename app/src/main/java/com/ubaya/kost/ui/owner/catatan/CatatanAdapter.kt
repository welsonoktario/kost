package com.ubaya.kost.ui.owner.catatan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Catatan
import com.ubaya.kost.databinding.CardCatatanBinding

class CatatanAdapter(private val data: ArrayList<Catatan>) :
    RecyclerView.Adapter<CatatanAdapter.CatatanViewHolder>() {

    inner class CatatanViewHolder(private val binding: CardCatatanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(catatan: Catatan) {
            binding.cardCatatanDesc.text = catatan.description
            binding.cardCatatanDate.text = catatan.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatatanViewHolder {
        val binding = CardCatatanBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CatatanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatatanViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}