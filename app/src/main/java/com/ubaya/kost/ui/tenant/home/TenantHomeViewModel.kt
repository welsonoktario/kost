package com.ubaya.kost.ui.tenant.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.*
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class TenantHomeViewModel(private val app: Application) : AndroidViewModel(app) {
    val gson: Gson = GsonBuilder().create()
    val isLoading = MutableLiveData<Boolean>()
    val msg = MutableLiveData<String>()

    private val _roomType = MutableLiveData<RoomType>()
    val roomType: LiveData<RoomType> = _roomType

    private val _room = MutableLiveData<Room>()
    val room: LiveData<Room> = _room

    private val _kost = MutableLiveData<Kost>()
    val kost: LiveData<Kost> = _kost

    private val _services = MutableLiveData<ArrayList<Service>>(arrayListOf())
    val services: LiveData<ArrayList<Service>> = _services

    private val _additionals = MutableLiveData<ArrayList<Additional>>(arrayListOf())
    val additionals: LiveData<ArrayList<Additional>> = _additionals

    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total

    fun setTotal(total: Int) {
        _total.value = total
    }

    fun loadDetailTenant(id: Int) {
        isLoading.value = true

        viewModelScope.launch {
            val url = VolleyClient.API_URL + "/tenants/$id"
            val request = object : JsonObjectRequest(url,
                { res ->
                    isLoading.value = false

                    val data = res.getJSONObject("data")
                    val tenant = data.getJSONObject("tenant")
                    val room = tenant.getJSONObject("room")
                    val servicesArr = tenant.getJSONArray("services")

                    for (i in 0 until servicesArr.length()) {
                        val svcJson = servicesArr.getJSONObject(i)
                        val svc = svcJson.getString("service")
                        _services.value = _services.value!!.apply {
                            this.add(gson.fromJson(svc))
                        }
                    }

                    _room.value = gson.fromJson(room.toString())
                    _roomType.value = gson.fromJson(room.getString("room_type"))
                    _additionals.value = gson.fromJson(tenant.getString("additionals"))
                    _total.value = data.getInt("total")
                    _kost.value = gson.fromJson(room.getString("kost"))

                    Global.authKost = _kost.value!!
                },
                { err ->
                    Log.d("err", err.toString())
                    try {
                        val data = JSONObject(String(err.networkResponse.data))

                        isLoading.value = false
                        msg.value = data.getString("msg")
                    } catch (e: Exception) {
                        msg.value = "Terjadi kesalahan sistem"
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

    fun gantiPassword(id: Int, pass: String) {
        isLoading.value = true
        val params = JSONObject()
        params.put("pass", pass)

        viewModelScope.launch {
            val url = VolleyClient.API_URL + "/tenants/$id/password"
            val request = object : JsonObjectRequest(
                Method.PUT,
                url,
                params,
                { res ->
                    isLoading.value = false
                    msg.value = res.getString("msg")
                },
                { err ->
                    Log.d("err", err.toString())
                    try {
                        val data = JSONObject(String(err.networkResponse.data))

                        isLoading.value = false
                        msg.value = data.getString("msg")
                    } catch (e: Exception) {
                        msg.value = "Terjadi kesalahan sistem"
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