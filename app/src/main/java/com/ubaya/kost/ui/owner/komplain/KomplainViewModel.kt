package com.ubaya.kost.ui.owner.komplain

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Complain
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class KomplainViewModel(private val app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _complains = MutableLiveData<ArrayList<Complain>>(arrayListOf())
    val complains: LiveData<ArrayList<Complain>> = _complains

    fun loadComplains() {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/complains?kost=${kost.id}"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    _complains.value = Gson().fromJson(res.getString("data"))
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

    fun updateComplain(position: Int, aksi: String) {
        isLoading.value = true
        error.value = Error(false, "")
        val id = _complains.value?.get(position)?.id
        val url = "${VolleyClient.API_URL}/complains/${id}"
        val params = JSONObject()
        params.put("aksi", aksi)

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                Method.PUT,
                url,
                params,
                {
                    isLoading.value = false
                    _complains.value = _complains.value!!.apply {
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
}