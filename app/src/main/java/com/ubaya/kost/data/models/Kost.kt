package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kost(
    val id: Int,
    val name: String,
    val address: String,
    val user: User
) : Parcelable {
    companion object {
        val listType = object : TypeToken<ArrayList<Kost>>() {}.type
    }
}