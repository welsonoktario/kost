package com.ubaya.kost.ui.tenant.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.GsonBuilder
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.*
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class TenantHomeViewModel(private val app: Application) : AndroidViewModel(app) {
    val gson = GsonBuilder().create()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())
    val msg = MutableLiveData<String>()

    private val _roomType = MutableLiveData<RoomType>()
    val roomType: LiveData<RoomType> = _roomType

    private val _room = MutableLiveData<Room>()
    val room: LiveData<Room> = _room

    private val _kost = MutableLiveData<Kost>()
    val kost: LiveData<Kost> = _kost

    private val _services = MutableLiveData<ArrayList<Service>>()
    val services: LiveData<ArrayList<Service>> = _services

    private val _additionals = MutableLiveData<ArrayList<Additional>>()
    val additionals: LiveData<ArrayList<Additional>> = _additionals

    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total

    fun setTotal(total: Int) {
        _total.value = total
    }

    fun loadDetailTenant(id: Int) {
        val newError = Error(false, "")
        isLoading.value = true
        error.value = newError

        viewModelScope.launch {
            val url = VolleyClient.API_URL + "/tenants/$id"
            val request = object : JsonObjectRequest(url,
                { res ->
                    isLoading.value = false
                    error.value = newError

                    val data = res.getJSONObject("data")
                    val tenant = data.getJSONObject("tenant")
                    val room = tenant.getJSONObject("room")

                    _room.value = gson.fromJson(room.toString())
                    _roomType.value = gson.fromJson(room.getString("room_type"))
                    _services.value = gson.fromJson(tenant.getString("services"))
                    _additionals.value = gson.fromJson(tenant.getString("additionals"))
                    _total.value = data.getInt("total")
                    _kost.value = gson.fromJson(room.getString("kost"))
                },
                { err ->
                    Log.d("err", err.toString())
                    try {
                        val data = JSONObject(String(err.networkResponse.data))

                        isLoading.value = false
                        newError.isError = true
                        newError.msg = data.getString("msg")

                        error.value = newError
                    } catch (e: Exception) {
                        isLoading.value = false
                        newError.isError = true
                        newError.msg = "Terjadi kesalahan sistem"

                        error.value = newError
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