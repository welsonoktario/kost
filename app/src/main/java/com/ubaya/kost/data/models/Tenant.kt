package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

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
) : Parcelable {
    fun diffFromDue(): Int {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val due = LocalDate.parse(dueDate, formatter)
        val current = LocalDate.parse(currentDate, formatter)

        return ChronoUnit.DAYS.between(current, due).toInt()
    }
}
