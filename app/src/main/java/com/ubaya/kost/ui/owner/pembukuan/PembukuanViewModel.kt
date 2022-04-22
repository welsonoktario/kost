package com.ubaya.kost.ui.owner.pembukuan

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
import com.ubaya.kost.data.models.Invoice
import com.ubaya.kost.data.models.Pengeluaran
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class PembukuanViewModel(private val app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _invoices = MutableLiveData<ArrayList<Invoice>>(arrayListOf())
    val invoices: LiveData<ArrayList<Invoice>> = _invoices

    private val _pengeluarans = MutableLiveData<ArrayList<Pengeluaran>>(arrayListOf())
    val pengeluarans: LiveData<ArrayList<Pengeluaran>> = _pengeluarans

    fun loadPembukuan() {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/pembukuans?kost=${kost.id}"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    val data = res.getJSONObject("data")
                    _invoices.value = Gson().fromJson(data.getString("invocies"))
                    _pengeluarans.value = Gson().fromJson(data.getString("pengeluarans"))
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