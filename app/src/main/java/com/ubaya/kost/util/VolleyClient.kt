package com.ubaya.kost.util

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyClient(context: Context) {
    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object : SingletonHolder<VolleyClient, Context>(::VolleyClient) {
        const val BASE_URL = "http://192.168.1.4/kost/public/api"
    }
}