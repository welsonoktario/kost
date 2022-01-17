package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kost(
    val id: Int? = null,
    val name: String? = null,
    val address: String? = null,
    var user: User? = null
) : Parcelable {
    companion object {
        val listType = object : TypeToken<ArrayList<Kost>>() {}.type
    }
}