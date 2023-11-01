package com.ryan.storyapp.ui.auth.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.ryan.storyapp.R

class EditTextPassword(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    init {
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                error = if (password.length < 8 || !isValidPassword(password)) {
                    context.getString(R.string.invalid_password)
                } else {
                    null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })
    }

    private fun isValidPassword(password: String): Boolean {
        val letterPattern = "[a-zA-Z]+".toRegex()
        val numberPattern = "\\d+".toRegex()
        val symbolPattern = "[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?]+".toRegex()

        val hasLetter = letterPattern.containsMatchIn(password)
        val hasNumber = numberPattern.containsMatchIn(password)
        val hasSymbol = symbolPattern.containsMatchIn(password)

        return hasLetter && hasNumber && hasSymbol
    }
}