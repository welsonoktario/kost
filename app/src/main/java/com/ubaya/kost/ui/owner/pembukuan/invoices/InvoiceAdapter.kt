package com.ubaya.kost.ui.owner.pembukuan.invoices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Invoice
import com.ubaya.kost.databinding.CardInvoiceBinding
import com.ubaya.kost.util.NumberUtil

class InvoiceAdapter(private val data: ArrayList<Invoice>, private val listener: InvoiceListener) :
    RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    interface InvoiceListener {
        fun onCardInvoiceClick(position: Int)
    }

    inner class InvoiceViewHolder(private val binding: CardInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardInvoice.setOnClickListener(this)
        }

        fun bind(invoice: Invoice) {
            binding.cardInvoiceTenant.text = invoice.tenant.user.name
            binding.cardInvoiceDate.text = invoice.date
            binding.cardInvoiceNo.text = invoice.tenant.room!!.noKamar.toString()
            binding.cardInvoiceTotal.text = NumberUtil().rupiah(invoice.total)
        }

        override fun onClick(v: View?) {
            listener.onCardInvoiceClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = CardInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return InvoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}