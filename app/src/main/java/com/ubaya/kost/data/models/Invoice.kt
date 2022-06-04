package com.ubaya.kost.data.models

import com.google.gson.annotations.SerializedName

data class Invoice(
    val id: Int,
    val total: Int,
    val tenant: Tenant? = null,
    val nama: String,
    @SerializedName("no_kamar") val noKamar: String,
    @SerializedName("tanggal_tagihan") val tanggalTagihan: String,
    val date: String,
    @SerializedName("invoice_details") val invoiceDetails: ArrayList<InvoiceDetail>
)
