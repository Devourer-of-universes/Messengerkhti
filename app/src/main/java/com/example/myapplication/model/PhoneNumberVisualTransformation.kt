package com.example.myapplication.model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val formattedText = StringBuilder()
        var index = 0

        for (char in originalText) {
            when (index) {
                0 -> formattedText.append("(")
                3 -> formattedText.append(") ")
                6 -> formattedText.append("-")
            }
            formattedText.append(char)
            index++
        }

        val phoneNumberOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 3 -> offset + 1
                    offset <= 6 -> offset + 3
                    offset <= 10 -> offset + 4
                    else -> offset + 4
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 4 -> offset - 1
                    offset <= 9 -> offset - 3
                    offset <= 14 -> offset - 4
                    else -> offset - 4
                }
            }
        }

        return TransformedText(AnnotatedString(formattedText.toString()), phoneNumberOffsetMapping)
    }
}