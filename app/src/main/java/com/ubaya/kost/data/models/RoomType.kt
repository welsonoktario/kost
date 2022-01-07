package com.ubaya.kost.data.models

import com.google.gson.annotations.SerializedName

data class RoomType(
    val id: Int? = 0,
    var name: String? = "",
    @SerializedName("room_count") var count: Int? = 0,
    var cost: Int? = 0
)
