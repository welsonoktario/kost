package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tenant(
    val id: Int,
    @SerializedName("entry_date")
    val entryDate: String,
    @SerializedName("leave_date")
    var leaveDate: String?,
    @SerializedName("due_date")
    var dueDate: String?,
    var status: Boolean,
    val ktp: String,
    val user: User
) : Parcelable
