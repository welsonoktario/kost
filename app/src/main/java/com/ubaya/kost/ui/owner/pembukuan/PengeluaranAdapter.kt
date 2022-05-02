package com.ubaya.kost.ui.owner.pembukuan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Pengeluaran
import com.ubaya.kost.databinding.CardPengeluaranBinding
import com.ubaya.kost.util.NumberUtil

class PengeluaranAdapter(private val data: ArrayList<Pengeluaran>) :
    RecyclerView.Adapter<PengeluaranAdapter.PengeluaranViewHolder>() {

    inner class PengeluaranViewHolder(private val binding: CardPengeluaranBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pengeluaran: Pengeluaran) {
            binding.cardPengeluaranDate.text = pengeluaran.date
            binding.cardPengeluaranDescription.text = pengeluaran.description
            binding.cardPengeluaranTotal.text = NumberUtil().rupiah(pengeluaran.nominal)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PengeluaranViewHolder {
        val binding =
            CardPengeluaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PengeluaranViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PengeluaranViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}