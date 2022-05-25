package com.ubaya.kost.ui.owner.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.R
import com.ubaya.kost.data.models.TenantService
import com.ubaya.kost.databinding.ListTenantServiceBinding

class ServicesAdapter(
    private val data: ArrayList<TenantService>,
    private val listener: ServicesListener
) :
    RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder>() {

    interface ServicesListener {
        fun onListClick(position: Int)
    }

    inner class ServicesViewHolder(private val binding: ListTenantServiceBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.listTenantServiceLayout.setOnClickListener(this)
        }

        fun bind(tenantService: TenantService) {
            binding.listTenantServiceNama.text = tenantService.tenant.user.name
            binding.listTenantServiceService.text = tenantService.service.name
            binding.listTenantServiceNo.text = tenantService.tenant.room!!.noKamar
            binding.listTenantServiceDate.text = "Pengajuan service untuk ${tenantService.date}"

            when(tenantService.status) {
                "diterima" -> {
                    binding.listTenantServiceStatus.setImageResource(R.drawable.ic_baseline_check_24)
                    binding.listTenantServiceStatus.visibility = View.VISIBLE
                }
                "ditolak" -> {
                    binding.listTenantServiceStatus.setImageResource(R.drawable.ic_baseline_close_24)
                    binding.listTenantServiceStatus.visibility = View.VISIBLE
                }
                else -> binding.listTenantServiceStatus.visibility = View.INVISIBLE
            }
        }

        override fun onClick(v: View?) {
            listener.onListClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val binding =
            ListTenantServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ServicesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size
}