package com.ubaya.kost.ui.owner.jatuh_tempo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class JatuhTempoViewModel(private val app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData<Boolean>()
    val isError = MutableLiveData<Boolean>()
    val msg = MutableLiveData<String>()

    private val _tenants = MutableLiveData<ArrayList<Tenant>>(arrayListOf())
    val tenants: LiveData<ArrayList<Tenant>> = _tenants

    fun loadTenants() {
        isLoading.value = true
        isError.value = false
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/tenants/jatuh-tempo?kost=${kost.id}"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    _tenants.value = Gson().fromJson(res.getString("data"))
                },
                { err ->
                    Log.d("ERR", String(err.networkResponse.data))
                    isLoading.value = false

                    try {
                        val data = JSONObject(String(err.networkResponse.data))
                        isError.value = true
                        msg.value = data.getString("msg")
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