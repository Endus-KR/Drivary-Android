package com.endus.drivary.presentation.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class DrivaryButtonStyle { Primary, Secondary }

private val ButtonShape = RoundedCornerShape(16.dp)

/** 차량 화면용 큰 버튼. 터치 영역은 최소 120×72dp */
@Composable
fun DrivaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DrivaryButtonStyle = DrivaryButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    val sizeModifier = modifier.defaultMinSize(minWidth = 120.dp, minHeight = 72.dp)
    val contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
    val label: @Composable () -> Unit = {
        Text(text = text, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold))
    }
    when (style) {
        DrivaryButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = sizeModifier,
            enabled = enabled,
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                disabledContainerColor = colors.surfaceVariant,
                disabledContentColor = colors.onSurfaceVariant,
            ),
            contentPadding = contentPadding,
        ) { label() }

        DrivaryButtonStyle.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = sizeModifier,
            enabled = enabled,
            shape = ButtonShape,
            border = BorderStroke(2.dp, colors.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.onSurface,
                disabledContentColor = colors.onSurfaceVariant,
            ),
            contentPadding = contentPadding,
        ) { label() }
    }
}
