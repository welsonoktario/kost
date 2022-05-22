package com.ubaya.kost.data.models

data class Transaksi(
    val invoice: Invoice? = null,
    val pengeluaran: Pengeluaran? = null
)
