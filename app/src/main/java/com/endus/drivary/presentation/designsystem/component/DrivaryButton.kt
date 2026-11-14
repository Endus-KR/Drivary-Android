package com.endus.drivary.presentation.designsystem.component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class DrivaryButtonStyle { Primary, Secondary }

/** 차량 화면용 큰 버튼. 터치 영역을 넉넉하게 잡는다 */
@Composable
fun DrivaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DrivaryButtonStyle = DrivaryButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val sizeModifier = modifier.defaultMinSize(minWidth = 120.dp, minHeight = 72.dp)
    val contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
    val label: @Composable () -> Unit = {
        Text(text = text, style = MaterialTheme.typography.titleLarge)
    }
    when (style) {
        DrivaryButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = sizeModifier,
            enabled = enabled,
            contentPadding = contentPadding,
        ) { label() }

        DrivaryButtonStyle.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = sizeModifier,
            enabled = enabled,
            contentPadding = contentPadding,
        ) { label() }
    }
}
