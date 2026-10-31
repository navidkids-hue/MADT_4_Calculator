package com.madt.calculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madt.calculator.ui.theme.WinAccentBlue
import com.madt.calculator.ui.theme.WinAccentBlueDark
import com.madt.calculator.ui.theme.WinDarkButtonBorder
import com.madt.calculator.ui.theme.WinDarkDigitButton
import com.madt.calculator.ui.theme.WinDarkOperatorButton
import com.madt.calculator.ui.theme.WinDarkTextPrimary
import com.madt.calculator.ui.theme.WinLightButtonBorder
import com.madt.calculator.ui.theme.WinLightDigitButton
import com.madt.calculator.ui.theme.WinLightOperatorButton
import com.madt.calculator.ui.theme.WinLightTextPrimary

enum class ButtonType {
    DIGIT,
    OPERATOR,
    ACCENT
}

/**
 * Windows 11 styled Calculator Button.
 * Digit buttons are lighter than operator buttons; '=' button is accented in Windows blue.
 */
@Composable
fun CalculatorButton(
    text: String,
    type: ButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val containerColor = when (type) {
        ButtonType.ACCENT -> if (darkTheme) WinAccentBlueDark else WinAccentBlue
        ButtonType.DIGIT -> if (darkTheme) WinDarkDigitButton else WinLightDigitButton
        ButtonType.OPERATOR -> if (darkTheme) WinDarkOperatorButton else WinLightOperatorButton
    }

    val contentColor = when (type) {
        ButtonType.ACCENT -> Color.White
        ButtonType.DIGIT -> if (darkTheme) WinDarkTextPrimary else WinLightTextPrimary
        ButtonType.OPERATOR -> if (darkTheme) WinDarkTextPrimary else WinLightTextPrimary
    }

    val borderColor = if (type == ButtonType.ACCENT) {
        null
    } else {
        if (darkTheme) WinDarkButtonBorder else WinLightButtonBorder
    }

    val fontSize = when {
        text.length > 3 -> 15.sp
        text == "1/x" || text == "x²" || text == "√x" -> 16.sp
        type == ButtonType.DIGIT -> 20.sp
        else -> 19.sp
    }

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = borderColor?.let { BorderStroke(0.8.dp, it) },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (type == ButtonType.DIGIT) 0.5.dp else 0.dp,
            pressedElevation = 0.dp
        ),
        modifier = modifier
            .fillMaxSize()
            .padding(2.5.dp)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                }
            )
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = if (type == ButtonType.DIGIT || type == ButtonType.ACCENT) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
