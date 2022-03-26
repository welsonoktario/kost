package com.ubaya.kost.ui.owner.dashboard.tenant

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

class TenantViewModel(private val app: Application) : AndroidViewModel(app) {
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())
    val msg = MutableLiveData<String>()

    private val _roomType = MutableLiveData<RoomType>()
    val roomType: LiveData<RoomType> = _roomType

    private val _room = MutableLiveData<Room>()
    val room: LiveData<Room> = _room

    private val _tenant = MutableLiveData<Tenant>()
    val tenant: LiveData<Tenant> = _tenant

    private val _services = MutableLiveData<ArrayList<Service>>()
    val services: LiveData<ArrayList<Service>> = _services

    private val _additionals = MutableLiveData<ArrayList<Additional>>(arrayListOf())
    val additionals: LiveData<ArrayList<Additional>> = _additionals

    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total

    fun setTenant(tenant: Tenant) {
        _tenant.value = tenant
    }

    fun setTotal(total: Int) {
        _total.value = _roomType.value!!.cost?.plus(total)
    }

    fun loadDetailTenant(id: Int) {
        val newError = Error(false, "")
        isLoading.value = true
        error.value = newError

        viewModelScope.launch {
            val url = VolleyClient.API_URL + "/rooms/$id"
            val request = object : JsonObjectRequest(url,
                { res ->
                    isLoading.value = false
                    error.value = newError

                    val data = res.getJSONObject("data")
                    val tenant = data.getJSONObject("tenant")
                    val services = tenant.getJSONArray("services")
                    val tempServices = arrayListOf<Service>()

                    for (i in 0 until services.length()) {
                        val s = services.getJSONObject(i)
                        tempServices.add(Gson().fromJson<Service>(s.getString("service")))
                    }

                    _roomType.value = Gson().fromJson(data.getString("room_type"))
                    _tenant.value = Gson().fromJson(tenant.toString())
                    _services.value = tempServices
                    _total.value = data.getInt("total")
                    _additionals.value = Gson().fromJson(tenant.getString("additionals"))
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

    fun konfirmasiPembayaran() {
        val newError = Error(false, "")
        isLoading.value = true
        error.value = newError

        viewModelScope.launch {
            val url = VolleyClient.API_URL + "/tenants/${_tenant.value!!.id}/konfirmasi"

            val request = object : JsonObjectRequest(url,
                { res ->
                    isLoading.value = false
                    error.value = newError

                    msg.value = res.getString("msg")
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
}