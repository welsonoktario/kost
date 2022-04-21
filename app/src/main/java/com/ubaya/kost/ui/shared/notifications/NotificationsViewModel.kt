package com.ubaya.kost.ui.shared.notifications

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Notification
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class NotificationsViewModel(private val app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _notifications = MutableLiveData<ArrayList<Notification>>(arrayListOf())
    val notifications: LiveData<ArrayList<Notification>> = _notifications

    fun loadNotifications() {
        isLoading.value = true
        error.value = Error(false, "")
        viewModelScope.launch {
            val url = "${VolleyClient.API_URL}/notifications?user=${Global.authUser.username}"
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    error.value = Error(false, "")
                    _notifications.value = Gson().fromJson(res.getString("data"))
                },
                { err ->
                    Log.d("ERR", String(err.networkResponse.data))
                    isLoading.value = false

                    try {
                        val data = JSONObject(String(err.networkResponse.data))
                        error.value = Error(true, data.getString("msg"))
                    } catch (e: Exception) {
                        Log.e("ERR", e.message.toString())
                    }
                }
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }

    fun readNotification(position: Int) {
        error.value = Error(false, "")
        val notification = _notifications.value!![position]

        viewModelScope.launch {
            val url = "${VolleyClient.API_URL}/notifications/${notification.id}"
            val request = object : JsonObjectRequest(
                Method.PUT,
                url,
                null,
                { res ->
                    error.value = Error(false, "")
                    _notifications.value = _notifications.value!!.apply {
                        this[position].isRead = true
                    }
                },
                { err ->
                    Log.d("ERR", String(err.networkResponse.data))

                    try {
                        val data = JSONObject(String(err.networkResponse.data))
                        error.value = Error(true, data.getString("msg"))
                    } catch (e: Exception) {
                        Log.e("ERR", e.message.toString())
                    }
                }
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }
}