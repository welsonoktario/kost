package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    val id: Int,
    @SerializedName("no_kamar") val noKamar: Int,
    var tenant: Tenant? = null,
    var roomType: RoomType? = null
) : Parcelable
