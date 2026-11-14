package com.endus.drivary.presentation.designsystem.theme

import androidx.compose.ui.graphics.Color

// 현대자동차 CI 색 (hyundai.com CI 안내 기준)
val HyundaiBlue = Color(0xFF002C5F)
val ActiveBlue = Color(0xFF00AAD2)
val HyundaiSand = Color(0xFFE4DCD3)
val LightSand = Color(0xFFF6F3F2)
val ActiveRed = Color(0xFFE63312)

// 차량 화면용 다크 배경: 현대 블루를 어둡게 내린 남색 계열
val Night = Color(0xFF07111F)
val NightSurface = Color(0xFF0F1D31)
val NightSurfaceHigh = Color(0xFF172A45)
val NightOutline = Color(0xFF2A3F5E)
val TextSecondary = Color(0xFFA9B6C8)

/** 운행 상태 색. 글자만 보지 않아도 구분되게 한다 */
object DrivaryStatusColors {
    /** 업무 */
    val Business = ActiveBlue

    /** 개인(비업무) */
    val Personal = Color(0xFF8E9BB0)

    /** 미처리. 가장 눈에 띄게 */
    val Unclassified = Color(0xFFFFB020)

    /** 빨간날(주말·공휴일) 운행 */
    val RedDay = Color(0xFFFF5A3C)
}
