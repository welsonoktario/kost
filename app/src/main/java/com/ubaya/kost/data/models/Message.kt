package com.ubaya.kost.data.models

import com.google.gson.annotations.SerializedName

data class Message(
    val id: Int,
    @SerializedName("is_owner") val isOwner: Boolean,
    val message: String,
    val tenant: Tenant? = null
)
