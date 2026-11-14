package com.endus.drivary.presentation.home

import com.endus.drivary.domain.model.RecordingStatus
import java.text.DecimalFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** 홈 상단의 운행 중 / 확정 대기 표시. 주차 중이면 null */
data class RecordingStatusUiModel(
    val title: String,
    val detail: String,
    val isConfirmPending: Boolean,
)

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.KOREAN)
private val kmFormat = DecimalFormat("#,##0.0")

fun RecordingStatus.toUiModel(
    now: Instant,
    zoneId: ZoneId = ZoneId.systemDefault(),
): RecordingStatusUiModel? = when (this) {
    RecordingStatus.Idle -> null

    is RecordingStatus.Driving -> RecordingStatusUiModel(
        title = "운행 중",
        detail = "${startedAt.atZone(zoneId).format(timeFormatter)} 출발 · 출발 계기판 ${kmFormat.format(odometerBeforeKm)}km",
        isConfirmPending = false,
    )

    is RecordingStatus.ConfirmPending -> {
        // 0초 아래로 내려가지 않게, 남은 시간은 올림해서 보여 준다
        val remainingMillis = Duration.between(now, confirmAt).toMillis().coerceAtLeast(0)
        val remainingSeconds = (remainingMillis + 999) / 1000
        RecordingStatusUiModel(
            title = "주차 · 운행 확정까지 ${remainingSeconds}초",
            detail = "이번 주행 ${kmFormat.format(odometerAfterKm - odometerBeforeKm)}km · 그 전에 다시 출발하면 같은 운행으로 이어져요",
            isConfirmPending = true,
        )
    }
}
