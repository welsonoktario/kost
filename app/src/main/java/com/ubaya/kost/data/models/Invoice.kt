package com.ubaya.kost.data.models

import com.google.gson.annotations.SerializedName

data class Invoice(
    val id: Int,
    val total: Int,
    val tenant: Tenant,
    @SerializedName("created_at") val date: String,
    val invoiceDetails: ArrayList<InvoiceDetail>
)
