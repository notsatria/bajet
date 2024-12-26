package com.notsatria.bajet.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
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
import com.notsatria.bajet.R
import com.notsatria.bajet.utils.LOCALE_ID
import java.text.NumberFormat

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
        prefix = { Text(text = stringResource(R.string.rp)) },
    )
}

class RupiahVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val rawAmount = text.text.replace(",", "").replace(".", "")
        val amount = if (rawAmount.isNotEmpty()) rawAmount.toLong() else 0L
        val formatted = NumberFormat.getNumberInstance(LOCALE_ID).format(amount)
        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return rawAmount.length.coerceAtMost(offset)
            }
        }
        return TransformedText(AnnotatedString(formatted), numberOffsetTranslator)
    }
}