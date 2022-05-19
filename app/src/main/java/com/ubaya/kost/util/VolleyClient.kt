package com.ubaya.kost.util

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyClient(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleyClient? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleyClient(context).also {
                    INSTANCE = it
                }
            }

        const val BASE_URL = "http://10.0.2.2/kost/public"
        const val API_URL = "$BASE_URL/api"
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}