package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String,
    val name: String,
    val phone: String,
    val type: String
) : Parcelable {
    companion object {
        val listType = object : TypeToken<ArrayList<User>>() {}.type
    }
}