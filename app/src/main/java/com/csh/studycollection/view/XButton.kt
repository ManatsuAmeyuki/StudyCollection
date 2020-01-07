package com.csh.studycollection.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class XButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        typeface = Typeface.createFromAsset(context.assets,"ZCOOLKuaiLe-Regular.ttf")
    }
}