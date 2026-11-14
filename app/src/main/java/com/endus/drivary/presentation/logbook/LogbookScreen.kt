package com.endus.drivary.presentation.logbook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.endus.drivary.presentation.designsystem.component.DrivaryButton
import com.endus.drivary.presentation.designsystem.component.DrivaryButtonStyle
import com.endus.drivary.presentation.designsystem.component.DrivaryEmptyState
import com.endus.drivary.presentation.designsystem.component.DrivaryTopBar
import com.endus.drivary.presentation.designsystem.theme.DrivaryTheme

/**
 * 월간 운행기록부 화면 자리. `feat/monthly-logbook`에서 ViewModel·Contract와 함께 채운다.
 */
@Composable
fun LogbookRoute(
    onNavigateBack: () -> Unit,
) {
    LogbookScreen(onNavigateBack = onNavigateBack)
}

@Composable
fun LogbookScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DrivaryTopBar(
            title = "월간 운행기록부",
            navigation = {
                DrivaryButton(
                    text = "뒤로",
                    onClick = onNavigateBack,
                    style = DrivaryButtonStyle.Secondary,
                )
            },
        )
        DrivaryEmptyState(message = "준비 중이에요")
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun LogbookScreenPreview() {
    DrivaryTheme {
        LogbookScreen(onNavigateBack = {})
    }
}
