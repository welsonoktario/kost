package com.ubaya.kost.ui.owner.dashboard.tenant

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ubaya.kost.R
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

                    _roomType.value = Gson().fromJson(data.getString("room_type"))
                    _tenant.value = Gson().fromJson(tenant.toString())
                    _services.value = Gson().fromJson(tenant.getString("services"))
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

    fun addTagihan(params: JSONObject) {
        val newError = Error(false, "")
        isLoading.value = true
        error.value = newError

        viewModelScope.launch {
            val url = VolleyClient.API_URL + "/tenants/${_tenant.value!!.id}/tagihan"

            val request = object : JsonObjectRequest(Method.POST, url, params,
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

    fun perpanjang() {
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