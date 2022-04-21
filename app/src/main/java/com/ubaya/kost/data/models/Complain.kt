package com.ubaya.kost.data.models

import com.google.gson.annotations.SerializedName

data class Complain(
    val id: Int,
    val description: String,
    var status: String = "pending",
    @SerializedName("created_at") val date: String,
    val tenant: Tenant
)
