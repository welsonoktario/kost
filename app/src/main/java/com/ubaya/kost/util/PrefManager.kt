package com.ubaya.kost.util

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.ubaya.kost.data.models.User

class PrefManager constructor(context: Context) {

    private val pref = context.getSharedPreferences("APP", Context.MODE_PRIVATE)

    var authToken: String?
        get() = pref.getString("AUTH_TOKEN", "")
        set(token) = pref.edit { putString("AUTH_TOKEN", token) }

    var authUser: User?
        get() = pref.getString("AUTH_USER", "")?.let { Gson().fromJson(it, User::class.java) }
        set(user) = pref.edit { putString("AUTH_USER", Gson().toJson(user)) }

    fun clear() = pref.edit { clear() }

    companion object : SingletonHolder<PrefManager, Context>(::PrefManager)
}
