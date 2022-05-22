package com.ubaya.kost.ui.owner.dashboard

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
import com.ubaya.kost.data.models.Room
import com.ubaya.kost.data.models.RoomType
import com.ubaya.kost.util.VolleyClient
import com.ubaya.kost.util.fromJson
import kotlinx.coroutines.launch
import org.json.JSONObject

class DashboardViewModel(private val app: Application) : AndroidViewModel(app) {
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData(Error())

    private val _kost = MutableLiveData<Kost>()
    val kost: LiveData<Kost> = _kost

    private val _roomTypes = MutableLiveData<ArrayList<RoomType>>()
    val roomTypes: LiveData<ArrayList<RoomType>> = _roomTypes

    private val _rooms = MutableLiveData<ArrayList<Room>>(arrayListOf())
    val rooms: LiveData<ArrayList<Room>> = _rooms

    val selectedRoomType = MutableLiveData<RoomType>()

    private fun setKost(data: Kost) {
        _kost.value = data
    }

    private fun setRoomTypes(data: ArrayList<RoomType>) {
        _roomTypes.value = data
    }

    fun setRooms(roomType: RoomType) {
        _rooms.value = roomTypes.value!!.find { type -> type.id == roomType.id }!!.rooms!!
    }

    fun loadData() {
        val newError = Error(false, "")
        isLoading.value = true
        error.value = newError

        viewModelScope.launch {
            val user = Global.authUser
            val url = VolleyClient.API_URL + "/kosts/" + user.username
            val request = object : JsonObjectRequest(url,
                { res ->
                    isLoading.value = false
                    error.value = newError
                    Log.d("RES", res.toString())

                    val data = res.getJSONObject("data")
                    val kost = Gson().fromJson(data.toString(), Kost::class.java)
                    val roomTypes =
                        Gson().fromJson<ArrayList<RoomType>>(data.getString("room_types"))
                    kost.user = user
                    setKost(kost)
                    setRoomTypes(roomTypes)
                    selectedRoomType.value = roomTypes[0]
                },
                { err ->
                    Log.e("ERR", err.message.toString())
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
}