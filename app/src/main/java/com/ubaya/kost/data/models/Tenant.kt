package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ubaya.kost.util.NumberUtil
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.parcelize.Parcelize
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
    var user: User,
    var room: Room? = null
) : Parcelable {

    fun lamaMenyewa(): Int {
        val entry = LocalDate.parse(entryDate)
        val leave = LocalDate.parse(leaveDate!!)

        return entry.monthsUntil(leave)
    }

    fun diffFromDue(): Int {
        val tz = TimeZone.currentSystemDefault()
        val due = LocalDate.parse(dueDate!!)
        val currentDateTime = Clock.System.now().toLocalDateTime(tz).toString().split("T")
        val currentDate = LocalDate.parse(currentDateTime[0])

        return currentDate.daysUntil(due)
    }

    fun perpanjangan(durasi: Int): String {
        val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val ld = LocalDate.parse(leaveDate!!)
        val newLd = ld.plus(durasi, DateTimeUnit.MONTH)

        return newLd.toJavaLocalDate().format(df)
    }

    fun konfirmasi(): String {
        val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val dd = LocalDate.parse(dueDate!!)
        val newDd = dd.plus(1, DateTimeUnit.MONTH)

        return newDd.toJavaLocalDate().format(df)
    }

    fun tanggalTagihan(): String {
        val df = DateTimeFormatter.ofPattern("MM-yyyy", Locale.getDefault())
        val due = LocalDate.parse(dueDate!!)

        return due.toJavaLocalDate().format(df)
    }

    fun telat(): Int {
        val tz = TimeZone.currentSystemDefault()
        val due = LocalDate.parse(dueDate!!)
        val currentDateTime = Clock.System.now().toLocalDateTime(tz).toString().split("T")
        val currentDate = LocalDate.parse(currentDateTime[0])

        return due.daysUntil(currentDate)
    }

    fun nominalTelat(nominal: Int) = NumberUtil().rupiah(telat() * nominal)
}
