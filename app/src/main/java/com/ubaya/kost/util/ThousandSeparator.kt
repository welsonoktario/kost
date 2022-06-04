package com.ubaya.kost.util

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class ThousandSeparator(private val input: TextInputEditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        input.removeTextChangedListener(this)

        input.setText(NumberUtil().thousand(s.toString()))
        input.setSelection(input.text!!.length)

        input.addTextChangedListener(this)
    }
}