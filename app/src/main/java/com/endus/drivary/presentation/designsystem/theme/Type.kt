package com.endus.drivary.presentation.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 차량 가로 대화면 기준 글자 크기. 운전 중에 힐끗 봐도 읽히도록 기본값보다 한두 단계 크다.
 * 핵심 숫자(주행거리, 합계, 비율)는 display, 제목은 headline, 표·버튼은 title을 쓴다.
 */
private fun style(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontSize = size.sp, lineHeight = line.sp, fontWeight = weight)

val Typography = Typography(
    displayLarge = style(72, 80, FontWeight.Bold),
    displayMedium = style(60, 68, FontWeight.Bold),
    displaySmall = style(48, 56, FontWeight.Bold),
    headlineLarge = style(40, 48, FontWeight.Bold),
    headlineMedium = style(34, 42, FontWeight.Bold),
    headlineSmall = style(30, 38, FontWeight.SemiBold),
    titleLarge = style(26, 34, FontWeight.Medium),
    titleMedium = style(22, 30, FontWeight.Medium),
    titleSmall = style(20, 28, FontWeight.Medium),
    bodyLarge = style(22, 30),
    bodyMedium = style(20, 28),
    bodySmall = style(18, 24),
    labelLarge = style(22, 28, FontWeight.SemiBold),
    labelMedium = style(18, 24, FontWeight.SemiBold),
    labelSmall = style(16, 20, FontWeight.SemiBold),
)
