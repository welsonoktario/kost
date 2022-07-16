package com.ubaya.kost.ui.owner.jatuh_tempo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.databinding.CardJatuhTempoBinding

class JatuhTempoAdapter(private val data: ArrayList<Tenant>) :
    RecyclerView.Adapter<JatuhTempoAdapter.JatuhTempoViewHolder>() {

    inner class JatuhTempoViewHolder(private val binding: CardJatuhTempoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tenant: Tenant) {
            binding.cardJatuhTempoRoom.text = tenant.room?.noKamar
            binding.cardJatuhTempoTenant.text = tenant.user.name
            binding.cardJatuhTempoTanggal.text = tenant.dueDate
            binding.cardJatuhTempoLama.text =
                "Tagihan jatuh tempo dalam ${tenant.diffFromDue()} hari"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JatuhTempoViewHolder {
        val binding =
            CardJatuhTempoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return JatuhTempoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JatuhTempoViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}