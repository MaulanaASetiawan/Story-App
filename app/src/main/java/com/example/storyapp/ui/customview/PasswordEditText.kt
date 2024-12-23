package com.example.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R

class PasswordEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatEditText(context, attrs) {
    init {
        background = null
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = text?.toString()
                if (!password.isNullOrEmpty() && password.length < 8) {
                    error = context.getString(R.string.password_length_error)
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}