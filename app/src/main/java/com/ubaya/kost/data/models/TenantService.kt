package com.ubaya.kost.data.models

import com.google.gson.annotations.SerializedName

data class TenantService(
    val id: Int,
    val service: Service,
    val tenant: Tenant,
    var status: String = "pending",
    @SerializedName("tanggal") val date: String
)
