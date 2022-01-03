package com.ubaya.kost.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    val id: Int,
    val detail: String,
    val cost: Int,
    val kost: Kost
) : Parcelable
