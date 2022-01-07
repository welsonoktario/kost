package com.ubaya.kost.ui.auth.register

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.kost.data.models.RoomType
import com.ubaya.kost.databinding.CardRegisterJenisKamarBinding

class RoomTypeAdapter(
    private val data: ArrayList<RoomType>,
    private val listener: CardJenisClickListener
) : RecyclerView.Adapter<RoomTypeAdapter.RoomTypeHolder>() {

    interface CardJenisClickListener {
        fun onRemoveJenisClick(position: Int)
        fun onNamaJenisChanged(position: Int, newValue: String)
        fun onHargaJenisChanged(position: Int, newValue: Int)
        fun onJumlahJenisChanged(position: Int, newValue: Int)
    }

    inner class RoomTypeHolder(private val binding: CardRegisterJenisKamarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(roomType: RoomType) {
            binding.cardRegisterJenisNama.setText(roomType.name)

            if (roomType.count == 0) {
                binding.cardRegisterJenisJumlah.setText("")
            } else {
                binding.cardRegisterJenisJumlah.setText(roomType.count.toString())
            }

            if (roomType.cost == 0) {
                binding.cardRegisterJenisHarga.setText("")
            } else {
                binding.cardRegisterJenisHarga.setText(roomType.count.toString())
            }

            binding.cardRegisterJenisRemove.setOnClickListener {
                listener.onRemoveJenisClick(adapterPosition)
            }

            binding.cardRegisterJenisNama.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.toString() != "") {
                        binding.cardRegisterJenisNama.error = null
                        val newValue = s.toString()
                        listener.onNamaJenisChanged(adapterPosition, newValue)
                    } else {
                        binding.cardRegisterJenisNama.error = "Jenis kamar tidak boleh kosong"
                    }
                }
            })

            binding.cardRegisterJenisJumlah.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.toString() != "" && s.isDigitsOnly()) {
                        binding.cardRegisterJenisLayoutJumlah.error = null
                        val newValue = s.toString().toInt()
                        listener.onJumlahJenisChanged(adapterPosition, newValue)
                    } else {
                        binding.cardRegisterJenisLayoutJumlah.error = "Jumlah harus berupa angka"
                    }
                }
            })

            binding.cardRegisterJenisHarga.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.toString() != "" && s.isDigitsOnly()) {
                        binding.cardRegisterJenisLayoutHarga.error = null
                        val newValue = s.toString().toInt()
                        listener.onHargaJenisChanged(adapterPosition, newValue)
                    } else {
                        binding.cardRegisterJenisLayoutHarga.error = "Harga harus berupa angka"
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomTypeHolder {
        val binding = CardRegisterJenisKamarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RoomTypeHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomTypeHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount() = data.size
}