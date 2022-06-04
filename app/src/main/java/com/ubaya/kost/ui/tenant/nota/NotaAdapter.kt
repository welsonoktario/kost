package com.ubaya.kost.ui.tenant.nota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Invoice
import com.ubaya.kost.databinding.CardInvoiceBinding
import com.ubaya.kost.util.NumberUtil

class NotaAdapter(private val data: ArrayList<Invoice>, private val listener: NotaListener) :
    RecyclerView.Adapter<NotaAdapter.NotaViewHolder>() {

    interface NotaListener {
        fun onCardNotaClick(position: Int)
    }

    inner class NotaViewHolder(private val binding: CardInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardInvoice.setOnClickListener(this)
        }

        fun bind(invoice: Invoice) {
            binding.cardInvoiceTenant.text = invoice.nama
            binding.cardInvoiceDate.text = invoice.date
            binding.cardInvoiceTotal.text = NumberUtil().rupiah(invoice.total)
            binding.cardInvoiceNo.visibility = View.GONE
        }

        override fun onClick(v: View?) {
            listener.onCardNotaClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val binding = CardInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotaAdapter.NotaViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}