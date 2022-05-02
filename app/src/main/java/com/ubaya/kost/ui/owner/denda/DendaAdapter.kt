package com.ubaya.kost.ui.owner.denda

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Kost
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.databinding.CardDendaBinding

class DendaAdapter(private val data: ArrayList<Tenant>, private val kost: Kost) :
    RecyclerView.Adapter<DendaAdapter.DendaViewHolder>() {

    inner class DendaViewHolder(private val binding: CardDendaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tenant: Tenant) {
            binding.cardDendaTenant.text = tenant.user.name
            binding.cardDendaTelat.text = "Telat membayar ${tenant.telat()} hari"
            binding.cardDendaNominal.text = kost.nominalDenda?.let { tenant.nominalTelat(it) }
            binding.cardDendaRoom.text = tenant.id.toString()
            binding.cardDendaTanggal.text = tenant.dueDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DendaViewHolder {
        val binding = CardDendaBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DendaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DendaViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}