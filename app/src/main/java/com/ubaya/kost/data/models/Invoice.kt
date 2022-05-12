package com.ubaya.kost.data.models

import com.google.gson.annotations.SerializedName

data class Invoice(
    val id: Int,
    val total: Int,
    val tenant: Tenant,
    val date: String,
    @SerializedName("invoice_details") val invoiceDetails: ArrayList<InvoiceDetail>
)
