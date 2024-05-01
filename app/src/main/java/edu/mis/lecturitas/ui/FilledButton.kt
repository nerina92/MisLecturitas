package edu.mis.lecturitas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FilledButton(onClick: () -> Unit, text:String, color: Color, borderColor:Color) {
    FilledTonalButton(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        border = BorderStroke(3.dp, borderColor)
    )
    {
        Text(text)
    }
}

@Preview (showBackground = true)
@Composable
private fun FilledButtonPreview() {
    FilledButton(onClick = { }, text = "Button", color = Color.Blue, borderColor = Color.Black)
}