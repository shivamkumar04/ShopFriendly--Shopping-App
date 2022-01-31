package android.example.shopfriendly.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class sfedittext(context: Context,attr:AttributeSet):AppCompatEditText(context,attr) {
    init {
        applyFont()
    }
    private fun applyFont() {
        val typeface: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        setTypeface(typeface)
    }

}