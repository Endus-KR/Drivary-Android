package com.endus.drivary.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.endus.drivary.presentation.designsystem.theme.DrivaryStatusColors
import com.endus.drivary.presentation.designsystem.theme.DrivaryTheme

/** 운행 중 / 확정 대기 상태. 색 점으로 상태를 먼저 보여 준다 (운행 중 파랑, 확정 대기 호박색) */
@Composable
fun RecordingStatusBanner(
    status: RecordingStatusUiModel,
    modifier: Modifier = Modifier,
) {
    val accent = if (status.isConfirmPending) DrivaryStatusColors.Unclassified else MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(accent.copy(alpha = 0.12f), shape)
            .border(2.dp, accent.copy(alpha = 0.6f), shape)
            .padding(horizontal = 28.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(accent, CircleShape),
        )
        Column {
            Text(text = status.title, style = MaterialTheme.typography.headlineMedium, color = accent)
            Text(
                text = status.detail,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
private fun RecordingStatusBannerPreview() {
    DrivaryTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RecordingStatusBanner(
                RecordingStatusUiModel(title = "운행 중", detail = "10:20 출발 · 출발 계기판 12,345.6km", isConfirmPending = false),
            )
            RecordingStatusBanner(
                RecordingStatusUiModel(
                    title = "주차 · 운행 확정까지 7초",
                    detail = "이번 주행 3.0km · 그 전에 다시 출발하면 같은 운행으로 이어져요",
                    isConfirmPending = true,
                ),
            )
        }
    }
}
