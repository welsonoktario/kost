package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Parcelize
data class RoomType(
    val id: Int? = null,
    var name: String? = "",
    @SerializedName("room_count") var count: Int? = 0,
    var cost: Int? = 0,
    var rooms: ArrayList<Room>? = null
) : Parcelable
