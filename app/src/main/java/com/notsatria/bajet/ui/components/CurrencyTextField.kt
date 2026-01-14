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
    onAmountChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = amount,
        onValueChange = { input ->
            // Extract only digits from the input
            val digitsOnly = input.filter { it.isDigit() }
            
            // Remove leading zeros, but keep at least "0"
            val cleanAmount = digitsOnly.trimStart('0').ifEmpty { "0" }
            
            onAmountChange(cleanAmount)
        },
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
        
        // Empty input
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        
        // Only format if it contains only digits
        if (originalText.isDigitsOnly().not()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // Prevent overflow by limiting to 18 digits (max for Long)
        val safeText = if (originalText.length > 18) {
            originalText.substring(0, 18)
        } else {
            originalText
        }

        return try {
            val formattedText = numberFormatter.format(safeText.toLong())
            TransformedText(
                AnnotatedString(formattedText),
                CurrencyOffsetMapping(safeText, formattedText)
            )
        } catch (e: Exception) {
            // Fallback if formatting fails
            TransformedText(text, OffsetMapping.Identity)
        }
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
                // If a digit is not found, fallback to empty list for Identity mapping
                return emptyList()
            }
        }
        return digitIndices
    }

    override fun originalToTransformed(offset: Int): Int {
        if (indices.isEmpty()) return offset
        if (offset >= originalLength) {
            return indices.lastOrNull()?.plus(1) ?: offset
        }
        return indices.getOrNull(offset) ?: offset
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (indices.isEmpty()) return offset
        val index = indices.indexOfFirst { it >= offset }.takeIf { it != -1 } ?: originalLength
        return index
    }
}