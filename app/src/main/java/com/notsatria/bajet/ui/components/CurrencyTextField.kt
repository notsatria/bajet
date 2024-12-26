package com.notsatria.bajet.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.text.isDigitsOnly
import com.notsatria.bajet.R
import com.notsatria.bajet.utils.LOCALE_ID
import java.text.NumberFormat
import java.util.Currency

@Composable
fun CurrencyTextField(
    modifier: Modifier = Modifier,
    amount: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = amount,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(R.string.amount))
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = RupiahVisualTransformation(),
    )
}

class RupiahVisualTransformation : VisualTransformation {
    private val numberFormatter = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance(LOCALE_ID)
        maximumFractionDigits = 0
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.trim()
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        if (originalText.isDigitsOnly().not()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedText = numberFormatter.format(originalText.toInt())
        return TransformedText(AnnotatedString(formattedText), CurrencyOffsetMapping(originalText, formattedText))
    }
}

class CurrencyOffsetMapping(originalText: String, formattedText: String) : OffsetMapping {
    private val originalLength: Int = originalText.length
    private val indices = findDigitIndices(originalText, formattedText)

    private fun findDigitIndices(firstString: String, secondString: String): List<Int> {
        val digitIndices = mutableListOf<Int>()
        var currentIndex = 0
        for (digit in firstString) {
            val index = secondString.indexOf(digit, currentIndex)
            if (index != -1) {
                digitIndices.add(index)
                currentIndex = index + 1
            } else {
                return emptyList()
            }
        }
        return digitIndices
    }

    override fun originalToTransformed(offset: Int): Int {
        if (offset >= originalLength) {
            return indices.last() + 1
        }

        return indices[offset]
    }

    override fun transformedToOriginal(offset: Int): Int {
        return indices.indexOfFirst { it >= offset }.takeIf { it != -1 } ?: originalLength
    }
}