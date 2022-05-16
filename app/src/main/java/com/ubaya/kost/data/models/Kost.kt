package com.ubaya.kost.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kost(
    val id: Int? = null,
    val name: String? = null,
    val address: String? = null,
    var user: User? = null,
    @SerializedName("nominal_denda") var nominalDenda: Int? = null,
    @SerializedName("interval_denda") var intervalDenda: Int? = null,
    @SerializedName("denda_berlaku") var dendaBerlaku: Int? = null
) : Parcelable