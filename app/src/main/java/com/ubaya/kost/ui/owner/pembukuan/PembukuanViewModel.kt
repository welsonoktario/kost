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
    val startDate = MutableLiveData<String>()
    val endDate = MutableLiveData<String>()

    private val _invoices = MutableLiveData<ArrayList<Invoice>>(arrayListOf())
    val invoices: LiveData<ArrayList<Invoice>> = _invoices

    private val _pengeluarans = MutableLiveData<ArrayList<Pengeluaran>>(arrayListOf())
    val pengeluarans: LiveData<ArrayList<Pengeluaran>> = _pengeluarans

    fun loadPembukuan() {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = if (startDate.value != null && endDate.value != null) {
            "${VolleyClient.API_URL}/pembukuans?kost=${kost.id}&start=${startDate.value}&end=${endDate.value}"
        } else {
            "${VolleyClient.API_URL}/pembukuans?kost=${kost.id}"
        }

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    Log.d("INVOICES", res.toString())
                    isLoading.value = false
                    val data = res.getJSONObject("data")
                    _invoices.value = Gson().fromJson(data.getString("invoices"))
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

    fun addPengeluaran(date: String, description: String, nominal: Int) {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/pembukuans?kost=${kost.id}"
        val params = JSONObject().apply {
            this.put("kost", kost.id)
            this.put("date", date)
            this.put("description", description)
            this.put("nominal", nominal)
        }

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                Method.POST,
                url,
                params,
                { res ->
                    isLoading.value = false
                    _pengeluarans.value = _pengeluarans.value!!.apply {
                        val pengeluaran: Pengeluaran = Gson().fromJson(res.getString("data"))
                        this.add(0, pengeluaran)
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