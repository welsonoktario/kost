package com.ubaya.kost.util

import java.text.NumberFormat
import java.util.*

class NumberUtil {
    fun rupiah(nominal: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val result = formatter.format(nominal)

        return result.replace("Rp", "Rp ").substring(0, result.length - 2)
    }
}