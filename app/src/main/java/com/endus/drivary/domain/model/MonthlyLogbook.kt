package com.endus.drivary.domain.model

import java.time.LocalDate
import java.time.YearMonth

/**
 * 한 달 치 국세청 업무용승용차 운행기록부.
 *
 * @property rows 운행마다 한 줄. 개인·미처리 운행도 빠짐없이 들어간다
 */
data class MonthlyLogbook(
    val month: YearMonth,
    val driver: Driver,
    val rows: List<LogbookRow>,
    val summary: LogbookSummary,
) {
    /** 아직 분류하지 않은 운행 수. 0이어야 내보낼 수 있다 */
    val unclassifiedCount: Int
        get() = rows.count { it.entry == LogbookEntry.Unclassified }
}

/**
 * 운행기록부 한 줄. 계기판 값과 주행거리는 km 정수(반올림)다.
 *
 * 주행거리는 반올림한 주행 후 값 − 반올림한 주행 전 값이라,
 * 줄을 모두 더하면 첫 주행 전 값과 마지막 주행 후 값의 차이와 같다.
 */
data class LogbookRow(
    val tripId: Long,
    val date: LocalDate,
    val odometerBeforeKm: Long,
    val odometerAfterKm: Long,
    val distanceKm: Long,
    val entry: LogbookEntry,
)

/** 한 줄이 어느 칸에 들어가는지 */
sealed interface LogbookEntry {

    /** 업무 운행. 목적에 따라 출·퇴근용 또는 일반 업무용 칸에 거리가 들어간다 */
    data class Business(val purpose: TripPurpose) : LogbookEntry

    /** 개인 운행. 업무용 칸은 둘 다 0, 비고는 비운다 */
    data object Personal : LogbookEntry

    /** 아직 분류하지 않은 운행. 칸 구분을 모른다 */
    data object Unclassified : LogbookEntry
}

/** 출·퇴근용 칸에 들어가는 거리(km). 업무가 아니면 0 */
val LogbookRow.commuteKm: Long
    get() = (entry as? LogbookEntry.Business)
        ?.takeIf { it.purpose.usage == TripUsage.COMMUTE }
        ?.let { distanceKm } ?: 0L

/** 일반 업무용 칸에 들어가는 거리(km). 업무가 아니면 0 */
val LogbookRow.generalBusinessKm: Long
    get() = (entry as? LogbookEntry.Business)
        ?.takeIf { it.purpose.usage == TripUsage.GENERAL_BUSINESS }
        ?.let { distanceKm } ?: 0L

/** 비고. 업무 목적 이름만 쓰고, 개인·미처리는 비운다 */
val LogbookRow.note: String
    get() = (entry as? LogbookEntry.Business)?.purpose?.displayName.orEmpty()

/**
 * 이번 달 하단 합계.
 *
 * @property businessUseRatioPercent 업무용 사용거리 ÷ 총주행거리 × 100, 소수점 첫째 자리까지. 총주행거리가 0이면 0
 */
data class LogbookSummary(
    val totalDistanceKm: Long,
    val businessUseDistanceKm: Long,
    val businessUseRatioPercent: Double,
)
