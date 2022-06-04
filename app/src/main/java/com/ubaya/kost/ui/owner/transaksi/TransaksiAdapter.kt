package com.ubaya.kost.ui.owner.transaksi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Transaksi
import com.ubaya.kost.databinding.CardTransaksiBinding
import com.ubaya.kost.util.NumberUtil

class TransaksiAdapter(
    private val data: ArrayList<Transaksi>,
    private val listener: TransaksiListener
) :
    RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {

    interface TransaksiListener {
        fun onCardTransaksiClick(position: Int)
    }

    inner class TransaksiViewHolder(private val binding: CardTransaksiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaksi: Transaksi) {
            if (transaksi.invoice != null) {
                val invoice = transaksi.invoice

                binding.cardTransaksiType.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.primary
                    )
                )
                binding.cardTransaksiType.text = "Tagihan"
                binding.cardTransaksiTenantDate.text = invoice.nama
                binding.cardTransaksiDateDesc.text = invoice.date
                binding.cardTransaksiTotal.text = NumberUtil().rupiah(invoice.total)
                binding.cardTransaksiNo.text = invoice.noKamar
                binding.cardTransaksiNo.visibility = View.VISIBLE

                binding.cardTransaksi.setOnClickListener {
                    listener.onCardTransaksiClick(adapterPosition)
                }
            } else if (transaksi.pengeluaran != null) {
                val pengeluaran = transaksi.pengeluaran

                binding.cardTransaksiType.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.red_600
                    )
                )
                binding.cardTransaksiType.text = "Pengeluaran"
                binding.cardTransaksiTenantDate.text = pengeluaran.date
                binding.cardTransaksiDateDesc.text = pengeluaran.description
                binding.cardTransaksiTotal.text = NumberUtil().rupiah(pengeluaran.nominal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val binding =
            CardTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TransaksiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}