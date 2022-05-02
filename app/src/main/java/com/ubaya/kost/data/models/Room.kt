package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    val id: Int,
    val noKamar: Int,
    var tenant: Tenant? = null
) : Parcelable
