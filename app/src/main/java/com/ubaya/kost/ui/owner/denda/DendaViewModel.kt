package com.ubaya.kost.ui.owner.denda

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
import com.ubaya.kost.data.models.Kost
import com.ubaya.kost.data.models.Tenant
import com.ubaya.kost.util.PrefManager
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class DendaViewModel(private val app: Application) : AndroidViewModel(app) {
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())
    val msg = MutableLiveData("")

    private val _kost = MutableLiveData<Kost>()
    val kost: LiveData<Kost> = _kost

    private val _tenants = MutableLiveData<ArrayList<Tenant>>(arrayListOf())
    val tenants: LiveData<ArrayList<Tenant>> = _tenants

    fun loadDenda() {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/dendas?kost=${kost.id}"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    _kost.value = Global.authKost
                    _tenants.value = Gson().fromJson(res.getString("data"))
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

    fun updateDenda(nominal: Int, interval: Int, berlaku: Int) {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/dendas/${kost.id}"
        val params = JSONObject()
        params.put("nominal", nominal)
        params.put("interval", interval)
        params.put("berlaku", berlaku)

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                Method.PUT,
                url,
                params,
                { res ->
                    isLoading.value = false
                    val prefs = PrefManager.getInstance(app.applicationContext)
                    Global.authKost.apply {
                        this.intervalDenda = interval
                        this.nominalDenda = nominal
                        this.dendaBerlaku = berlaku
                    }
                    _kost.value = Global.authKost
                    prefs.authKost = Global.authKost
                    msg.value = res.getString("msg")
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
}