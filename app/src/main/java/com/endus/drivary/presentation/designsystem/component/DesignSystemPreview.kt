package com.endus.drivary.presentation.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.endus.drivary.presentation.designsystem.theme.DrivaryTheme

/** 공통 컴포넌트를 한 화면에서 보는 미리보기 */
@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun DesignSystemPreview() {
    DrivaryTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            DrivaryTopBar(
                title = "디자인 시스템",
                navigation = { DrivaryButton(text = "뒤로", onClick = {}, style = DrivaryButtonStyle.Secondary) },
                actions = { DrivaryButton(text = "운행기록부", onClick = {}) },
            )
            Column(
                modifier = Modifier.padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DrivaryButton(text = "업무", onClick = {})
                    DrivaryButton(text = "개인", onClick = {}, style = DrivaryButtonStyle.Secondary)
                    DrivaryButton(text = "내보내기", onClick = {}, enabled = false)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DrivaryTag(text = "영업", style = DrivaryTagStyle.Business)
                    DrivaryTag(text = "개인", style = DrivaryTagStyle.Personal)
                    DrivaryTag(text = "미처리", style = DrivaryTagStyle.Unclassified)
                    DrivaryTag(text = "토요일", style = DrivaryTagStyle.RedDay)
                }
                DrivaryCard {
                    Text(text = "업무사용비율", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "94.7%", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
