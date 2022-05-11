package com.ubaya.kost.ui.shared.chats

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.ChatRoom
import com.ubaya.kost.data.models.Error
import com.ubaya.kost.data.models.Message
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel(private val app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _chatRooms = MutableLiveData<ArrayList<ChatRoom>>(arrayListOf())
    val chatRooms: LiveData<ArrayList<ChatRoom>> = _chatRooms

    private val _chatRoom = MutableLiveData<ChatRoom>()
    val chatRoom: LiveData<ChatRoom> = _chatRoom

    private val _messages = MutableLiveData<ArrayList<Message>>(arrayListOf())
    val messages: LiveData<ArrayList<Message>> = _messages

    fun loadChatRooms() {
        isLoading.value = true
        error.value = Error(false, "")
        val kost = Global.authKost
        val url = "${VolleyClient.API_URL}/chats/${kost.id}"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    Log.d("CR", res.toString())
                    isLoading.value = false
                    _chatRooms.value = Gson().fromJson(res.getString("data"))
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

    fun loadMessages(kost: Int, tenant: Int) {
        isLoading.value = true
        error.value = Error(false, "")
        val url = "${VolleyClient.API_URL}/chats?kost=$kost&tenant=$tenant"

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                url,
                { res ->
                    isLoading.value = false
                    val data = res.getJSONObject("data")
                    _chatRoom.value = Gson().fromJson(data.toString())
                    _messages.value = Gson().fromJson(data.getString("messages"))
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

    fun addMessage(id: Int, message: String) {
        isLoading.value = true
        error.value = Error(false, "")
        val url = "${VolleyClient.API_URL}/chats/$id"
        val params = JSONObject()
        params.put("message", message)

        viewModelScope.launch {
            val request = object : JsonObjectRequest(
                Method.POST,
                url,
                params,
                { res ->
                    isLoading.value = false
                    val msg: Message = Gson().fromJson(res.getString("data"))
                    _messages.value = _messages.value!!.apply {
                        this.add(msg)
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