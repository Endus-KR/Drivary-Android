package com.endus.drivary.presentation.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.endus.drivary.presentation.designsystem.component.DrivaryCard

/** 운행 중 / 확정 대기 상태 */
@Composable
fun RecordingStatusBanner(
    status: RecordingStatusUiModel,
    modifier: Modifier = Modifier,
) {
    DrivaryCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = status.title,
            style = MaterialTheme.typography.headlineMedium,
            color = if (status.isConfirmPending) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.primary
            },
        )
        Text(
            text = status.detail,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
