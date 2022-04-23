package com.ubaya.kost.data.models

data class Invoice(
    val id: Int,
    val total: Int,
    val tenant: Tenant,
    val date: String,
    val invoiceDetails: ArrayList<InvoiceDetail>
)
