package com.endus.drivary.presentation.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * 차량 화면용 다크 테마. 현대자동차 CI 색(액티브 블루 강조, 라이트 샌드 글자)을 쓴다.
 * 시연 영상이 기기마다 달라지지 않도록 동적 색상(Material You)과 라이트 테마는 쓰지 않는다.
 */
private val DrivaryColorScheme = darkColorScheme(
    primary = ActiveBlue,
    onPrimary = Night,
    primaryContainer = HyundaiBlue,
    onPrimaryContainer = LightSand,
    secondary = HyundaiSand,
    onSecondary = Night,
    tertiary = DrivaryStatusColors.Unclassified,
    onTertiary = Night,
    background = Night,
    onBackground = LightSand,
    // 화면 바탕(MainActivity의 Surface)은 가장 어두운 남색, 카드는 한 단계 밝게
    surface = Night,
    onSurface = LightSand,
    surfaceVariant = NightSurfaceHigh,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = Night,
    surfaceContainerLow = NightSurface,
    surfaceContainer = NightSurface,
    surfaceContainerHigh = NightSurfaceHigh,
    surfaceContainerHighest = NightSurfaceHigh,
    outline = NightOutline,
    outlineVariant = NightOutline,
    error = DrivaryStatusColors.RedDay,
    onError = Night,
)

@Composable
fun DrivaryTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DrivaryColorScheme,
        typography = Typography,
        content = content,
    )
}
