package com.endus.drivary.presentation.logbook

import com.endus.drivary.domain.model.LogbookEntry
import com.endus.drivary.domain.model.LogbookRow
import com.endus.drivary.domain.model.MonthlyLogbook
import com.endus.drivary.domain.model.commuteKm
import com.endus.drivary.domain.model.generalBusinessKm
import com.endus.drivary.domain.model.note
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

/** 운행기록부 한 줄. 국세청 서식 칸 순서(사용자 칸 제외) */
data class LogbookRowUiModel(
    val id: Long,
    val dateText: String,
    val odometerBeforeText: String,
    val odometerAfterText: String,
    val distanceText: String,
    val commuteText: String,
    val generalBusinessText: String,
    val noteText: String,
    /** 미처리 운행이면 칸 구분을 모르니 강조해 보여 준다 */
    val isUnclassified: Boolean,
)

private val dateFormatter = DateTimeFormatter.ofPattern("M/d(E)", Locale.KOREAN)
private val monthFormatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN)
private val kmFormat = DecimalFormat("#,##0")

fun MonthlyLogbook.toUiState(): LogbookUiState {
    val unclassified = unclassifiedCount
    return LogbookUiState(
        monthText = month.format(monthFormatter),
        driverText = "사용자 ${driver.department} · ${driver.name}",
        rows = rows.map { it.toUiModel() },
        totalDistanceText = "${kmFormat.format(summary.totalDistanceKm)} km",
        businessUseDistanceText = "${kmFormat.format(summary.businessUseDistanceKm)} km",
        businessUseRatioText = "%.1f%%".format(Locale.ROOT, summary.businessUseRatioPercent),
        isExportEnabled = rows.isNotEmpty() && unclassified == 0,
        exportBlockedMessage = when {
            rows.isEmpty() -> "이 달에는 내보낼 운행이 없어요"
            unclassified > 0 -> "미처리 운행 ${unclassified}건을 먼저 분류해 주세요"
            else -> null
        },
    )
}

fun LogbookRow.toUiModel(): LogbookRowUiModel {
    val isUnclassified = entry == LogbookEntry.Unclassified
    return LogbookRowUiModel(
        id = tripId,
        dateText = date.format(dateFormatter),
        odometerBeforeText = kmFormat.format(odometerBeforeKm),
        odometerAfterText = kmFormat.format(odometerAfterKm),
        distanceText = kmFormat.format(distanceKm),
        commuteText = if (isUnclassified) "-" else kmFormat.format(commuteKm),
        generalBusinessText = if (isUnclassified) "-" else kmFormat.format(generalBusinessKm),
        noteText = if (isUnclassified) "미처리" else note,
        isUnclassified = isUnclassified,
    )
}
