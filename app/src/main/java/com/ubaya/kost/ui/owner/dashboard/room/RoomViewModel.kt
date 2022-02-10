package com.ubaya.kost.ui.owner.dashboard.room

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.*
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset

class RoomViewModel(private val app: Application) : AndroidViewModel(app) {
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _room = MutableLiveData<Room>()
    val room: LiveData<Room> = _room

    private val _tenant = MutableLiveData<Tenant>()
    val tenant: LiveData<Tenant> = _tenant

    private val _services = MutableLiveData<ArrayList<Service>>()
    val services: LiveData<ArrayList<Service>> = _services

    fun setRoom(room: Room) {
        _room.value = room
    }

    fun setTenant(tenant: Tenant) {
        _tenant.value = tenant
    }

    fun loadServices(kost: Kost) {
        val newError = Error(false, "")
        isLoading.value = true
        error.value = newError

        viewModelScope.launch {
            val url = VolleyClient.BASE_URL + "/services?kost=${kost.id}"
            val request = object : JsonObjectRequest(url,
                { res ->
                    Log.d("RES", res.toString())
                    isLoading.value = false
                    error.value = newError

                    val services = Gson().fromJson<ArrayList<Service>>(
                        res.getString("data")
                    )
                    _services.value = services
                },
                { err ->
                    val data = JSONObject(String(err.networkResponse.data))

                    isLoading.value = false
                    newError.isError = true
                    newError.msg = data.getString("msg")

                    error.value = newError
                }
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }

    fun loadRoom(id: Int) {
        val newError = Error(false, "")
        isLoading.value = true
        error.value = newError

        viewModelScope.launch {
            val url = VolleyClient.BASE_URL + "/rooms/$id"
            val request = object : JsonObjectRequest(url,
                { res ->
                    isLoading.value = false
                    error.value = newError

                    val data = res.getJSONObject("data")
                    _room.value = Gson().fromJson(data.getString("room"), Room::class.java)
                    _tenant.value = _room.value!!.tenant!!
                },
                { err ->
                    val data = JSONObject(String(err.networkResponse.data))

                    isLoading.value = false
                    newError.isError = true
                    newError.msg = data.getString("msg")

                    error.value = newError
                }
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }

    fun addTenant(params: JSONObject) {
        val newError = Error(false, "")
        isLoading.value = true
        error.value = newError

        viewModelScope.launch {
            val url = VolleyClient.BASE_URL + "/tenants"

            val request = object : JsonObjectRequest(Method.POST, url, params,
                { res ->
                    Log.d("RES", res.toString())
                    isLoading.value = false
                    error.value = newError

                    try {
                        val room = Gson().fromJson(res.toString(), Room::class.java)

                        setRoom(room)
                    } catch (e: Exception) {
                        Log.e("ERROR", e.message.toString())
                    }
                },
                { err ->
                    Log.e("ERROR", String(err.networkResponse.data, Charset.defaultCharset()))
                    val data =
                        JSONObject(String(err.networkResponse.data, Charset.defaultCharset()))

                    isLoading.value = false
                    newError.isError = true
                    newError.msg = data.getString("msg")

                    error.value = newError
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