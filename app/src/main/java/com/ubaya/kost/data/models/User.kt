package com.ubaya.kost.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String,
    val name: String,
    val phone: String,
    val type: String
) : Parcelable