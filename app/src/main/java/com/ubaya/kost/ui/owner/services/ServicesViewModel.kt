package com.ubaya.kost.ui.owner.services

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
import com.ubaya.kost.data.models.Service
import com.ubaya.kost.data.models.TenantService
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class ServicesViewModel(private val app: Application) : AndroidViewModel(app) {
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _services = MutableLiveData<ArrayList<Service>>(arrayListOf())
    val services: LiveData<ArrayList<Service>> = _services

    private val _tenantServices = MutableLiveData<ArrayList<TenantService>>(arrayListOf())
    val tenantService: LiveData<ArrayList<TenantService>> = _tenantServices

    fun loadPengajuanServices() {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/tenant-service/${kost.id}"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    _tenantServices.value = Gson().fromJson(res.getString("data"))
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
                },
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }

    fun updatePengajuanService(position: Int, aksi: String, alasan: String? = null) {
        isLoading.value = true
        error.value = Error(false, "")
        val id = _tenantServices.value?.get(position)?.id
        val url = "${VolleyClient.API_URL}/tenant-service/${id}"
        val params = JSONObject()
        params.put("aksi", aksi)

        if (aksi === "ditolak") {
            params.put("alasan", alasan!!)
        }

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                Method.PUT,
                url,
                params,
                {
                    isLoading.value = false
                    _tenantServices.value = _tenantServices.value!!.apply {
                        this[position].status = aksi
                    }
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
                },
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }

    fun loadServices() {
        val url = "${VolleyClient.API_URL}/services?kost=${Global.authKost.id}"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    _services.value = Gson().fromJson(res.getString("data"))
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
                },
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }

    fun addService(name: String, description: String, cost: Int) {
        val url = "${VolleyClient.API_URL}/services"
        val params = JSONObject()
        params.put("kost", Global.authKost.id)
        params.put("name", name)
        params.put("description", description)
        params.put("cost", cost)

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                Method.POST,
                url,
                params,
                { res ->
                    _services.value = _services.value!!.apply {
                        this.add(0, Gson().fromJson(res.getString("data")))
                    }
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
                },
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }

    fun updateService(id: Int, name: String, description: String, cost: Int) {
        val url = "${VolleyClient.API_URL}/services/$id"
        val params = JSONObject()
        params.put("name", name)
        params.put("description", description)
        params.put("cost", cost)

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                Method.PUT,
                url,
                params,
                { res ->
                    val index = _services.value!!.indexOfFirst { service -> service.id == id }
                    _services.value = _services.value!!.apply {
                        this[index].let { service ->
                            service.name = name
                            service.description = description
                            service.cost = cost
                        }
                    }
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
                },
            ) {
                override fun getHeaders() = hashMapOf(
                    "Authorization" to "Bearer ${Global.authToken}"
                )
            }

            VolleyClient.getInstance(app.applicationContext).addToRequestQueue(request)
        }
    }
}