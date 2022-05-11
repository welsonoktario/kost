package com.ubaya.kost.ui.owner.catatan

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Catatan
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class CatatanViewModel(private val app: Application) : AndroidViewModel(app) {
    val isLoading = MutableLiveData<Boolean>()
    val isError = MutableLiveData<Boolean>()
    val msg = MutableLiveData<String>()

    private val _catatans = MutableLiveData<ArrayList<Catatan>>(arrayListOf())
    val catatans: LiveData<ArrayList<Catatan>> = _catatans

    fun loadCatatan() {
        isLoading.value = true
        isError.value = false
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/catatans?kost=${kost.id}"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    _catatans.value = Gson().fromJson(res.getString("data"))
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

    fun addCatatan(description: String, date: String) {
        isLoading.value = true
        isError.value = false
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/catatans"
        val params = JSONObject()
        params.put("kost", kost.id)
        params.put("description", description)
        params.put("date", date)

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                Method.POST,
                url,
                params,
                { res ->
                    isLoading.value = false
                    _catatans.value = _catatans.value!!.apply {
                        this.add(0, Gson().fromJson(res.getString("data")))
                    }
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