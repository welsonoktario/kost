package com.ubaya.kost.util

import com.google.gson.GsonBuilder

object Gson {
    val gson = GsonBuilder().setPrettyPrinting().create()
}