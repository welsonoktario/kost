package com.ubaya.kost.ui.owner.komplain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.R
import com.ubaya.kost.data.models.Complain
import com.ubaya.kost.databinding.CardComplainBinding

class KomplainAdapter(
    private val data: ArrayList<Complain>,
    private val listener: ComplainListener? = null
) : RecyclerView.Adapter<KomplainAdapter.ComplainViewHolder>() {

    interface ComplainListener {
        fun onCardComplainClick(position: Int)
    }

    inner class ComplainViewHolder(private val binding: CardComplainBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.cardComplainLayout.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onCardComplainClick(adapterPosition)
        }

        fun bind(complain: Complain) {
            binding.cardComplainTenant.text = complain.tenant.user.name
            binding.cardComplainNo.text = complain.tenant.id.toString()
            binding.cardComplainDescription.text = complain.description

            when(complain.status) {
                "diproses" -> {
                    binding.cardComplainStatus.setImageResource(R.drawable.ic_baseline_check_24)
                    binding.cardComplainStatus.visibility = View.VISIBLE
                }
                "ditolak" -> {
                    binding.cardComplainStatus.setImageResource(R.drawable.ic_baseline_close_24)
                    binding.cardComplainStatus.visibility = View.VISIBLE
                }
                else -> binding.cardComplainStatus.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplainViewHolder {
        val binding =
            CardComplainBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ComplainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplainViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}