package com.ubaya.kost.ui.owner.transaksi

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
import com.ubaya.kost.data.models.Transaksi
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class TransaksiViewModel(private val app: Application) : AndroidViewModel(app) {
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _transaksis = MutableLiveData<ArrayList<Transaksi>>(arrayListOf())
    val transaksis: LiveData<ArrayList<Transaksi>> = _transaksis

    fun loadTransaksi() {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/invoices/${kost.id}/history"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    _transaksis.value = Gson().fromJson(res.getString("data"))
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