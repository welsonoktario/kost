package com.ubaya.kost.util

import java.text.NumberFormat
import java.util.*

class NumberUtil {
    fun rupiah(nominal: Int): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val result = formatter.format(nominal)

        return result.replace("Rp", "Rp ").substring(0, result.length - 2)
    }

    fun thousand(text: String): String {
        val nominal = text.replace(".", "").toIntOrNull()

        if (text.isEmpty() || nominal == null) return text

        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val result = formatter.format(nominal)

        return result.replace("Rp", "").substring(0, result.length - 5)
    }
}