package com.ubaya.kost.ui.auth.register

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.Service
import com.ubaya.kost.databinding.CardRegisterServiceBinding
import com.ubaya.kost.util.NumberUtil

class ServiceAdapter(
    private val data: ArrayList<Service>,
    private val listener: CardServiceClickListener
) : RecyclerView.Adapter<ServiceAdapter.ServiceHolder>() {
    interface CardServiceClickListener {
        fun onRemoveServiceClick(position: Int)
        fun onNamaServiceChanged(position: Int, newValue: String)
        fun onDeskripsiServiceChanged(position: Int, newValue: String)
        fun onHargaServiceChanged(position: Int, newValue: Int)
    }

    inner class ServiceHolder(val binding: CardRegisterServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.cardRegisterServiceNama.setText("")
            binding.cardRegisterServiceDeskripsi.setText("")
            binding.cardRegisterServiceHarga.setText("")

            binding.cardRegisterServiceRemove.setOnClickListener {
                listener.onRemoveServiceClick(adapterPosition)
            }

            binding.cardRegisterServiceNama.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.toString() != "") {
                        binding.cardRegisterServiceLayoutNama.error = null
                        val newValue = s.toString()
                        listener.onNamaServiceChanged(adapterPosition, newValue)
                    } else {
                        binding.cardRegisterServiceLayoutNama.error =
                            "Jenis kamar tidak boleh kosong"
                    }
                }
            })

            binding.cardRegisterServiceDeskripsi.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.toString() != "") {
                        binding.cardRegisterServiceLayoutDeskripsi.error = null
                        val newValue = s.toString()
                        listener.onDeskripsiServiceChanged(adapterPosition, newValue)
                    } else {
                        binding.cardRegisterServiceLayoutDeskripsi.error =
                            "Jenis kamar tidak boleh kosong"
                    }
                }
            })

            binding.cardRegisterServiceHarga.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.cardRegisterServiceHarga.removeTextChangedListener(this)

                    binding.cardRegisterServiceHarga.setText(NumberUtil().thousand(s.toString()))
                    binding.cardRegisterServiceHarga.setSelection(binding.cardRegisterServiceHarga.text!!.length)

                    binding.cardRegisterServiceHarga.addTextChangedListener(this)
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.toString() != "" && s.isDigitsOnly()) {
                        binding.cardRegisterServiceLayoutHarga.error = null
                        val newValue = s.toString().replace(".", "").toInt()
                        listener.onHargaServiceChanged(adapterPosition, newValue)
                    } else {
                        binding.cardRegisterServiceLayoutHarga.error = "Harga harus berupa angka"
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceHolder {
        val binding =
            CardRegisterServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ServiceHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceHolder, position: Int) = holder.bind()

    override fun getItemCount() = data.size
}