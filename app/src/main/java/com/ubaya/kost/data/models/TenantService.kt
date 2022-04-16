package com.ubaya.kost.data.models

data class TenantService(
    val id: Int,
    val service: Service,
    val tenant: Tenant,
    var status: String = "pending",
)
