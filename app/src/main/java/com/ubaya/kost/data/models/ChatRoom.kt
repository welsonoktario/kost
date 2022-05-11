package com.ubaya.kost.data.models

data class ChatRoom(
    val id: Int,
    val tenant: Tenant,
    val messages: ArrayList<Message>
)
