package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    fun lamaMenyewa(): Int {
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val entry = LocalDate.parse(entryDate, df)
        val leave = LocalDate.parse(leaveDate, df)

        return leave.monthValue - entry.monthValue
    }

    fun diffFromDue(): Int {
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val entry = LocalDate.parse(entryDate, df)
        val currentDate = LocalDate.parse(LocalDate.now().toString(), df)

        return currentDate.dayOfMonth - entry.dayOfMonth
    }

    fun nextInvoice(): String {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val entry = LocalDate.parse(entryDate, df)
        val currentDate = LocalDate.parse(LocalDate.now().toString(), df)

        if (currentDate.dayOfMonth > entry.dayOfMonth) {
            currentDate.plusMonths(1)
        }

        cal.set(currentDate.year, currentDate.monthValue, entry.dayOfMonth)

        return sdf.format(cal.time)
    }

    fun perpanjangan(durasi: Int): String {
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val leaveDate = LocalDate.parse(leaveDate, df)

        leaveDate.plusMonths(durasi.toLong())

        return leaveDate.format(df)
    }

    fun tanggalTagihan(): String {
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val due = LocalDate.parse(dueDate, df)

        return due.format(DateTimeFormatter.ofPattern("MM-yyyy"))
    }
}
