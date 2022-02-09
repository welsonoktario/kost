package com.ubaya.kost.data.models

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

data class Service(
    val id: Int? = null,
    var name: String? = "",
    var description: String? = "",
    var cost: Int? = 0
) {
    companion object {
        val listType: Type = object : TypeToken<ArrayList<Service>>() {}.type
    }
}