package com.notsatria.bajet.ui.components

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun ClickableTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: String,
    onChange: (String) -> Unit,
    readOnly: Boolean,
    supportingText: String = "Empty Field",
    supportingTextCondition: () -> Boolean = { false },
    minLines: Int = 1,
    onClick: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                when (interaction) {
                    is PressInteraction.Press -> {
                        onClick()
                    }
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it) },
        readOnly = readOnly,
        placeholder = { Text(text = placeholder) },
        modifier = modifier,
        supportingText = {
            if (supportingTextCondition()) Text(
                text = supportingText,
                color = MaterialTheme.colorScheme.error
            )
        },
        label = { Text(text = placeholder) },
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        interactionSource = interactionSource
    )

}