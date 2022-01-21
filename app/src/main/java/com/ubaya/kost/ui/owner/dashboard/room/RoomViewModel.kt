package com.ubaya.kost.ui.owner.dashboard.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Room
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.util.ApiInterface
import com.ubaya.kost.util.VolleyClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class RoomViewModel(private val app: Application) : AndroidViewModel(app) {
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _room = MutableLiveData<Room>()
    val room: LiveData<Room> = _room

    private val _tenant = MutableLiveData<Tenant>()
    val tenant: LiveData<Tenant> = _tenant

    fun setRoom(room: Room) {
        _room.value = room
    }

    fun setTenant(tenant: Tenant) {
        _tenant.value = tenant
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
            ApiInterface.getInstance().addTenantToRoom()
        }
    }
}