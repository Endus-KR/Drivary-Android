package com.endus.drivary.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * 빨간날(주말·공휴일) 판단.
 * 공휴일은 2026년 관공서 공휴일(대체공휴일·임시공휴일 포함)을 고정으로 넣는다.
 */
object RedDayCalendar {

    private val HOLIDAYS_2026: Set<LocalDate> = listOf(
        "2026-01-01", // 신정
        "2026-02-16", "2026-02-17", "2026-02-18", // 설날
        "2026-03-01", "2026-03-02", // 삼일절, 대체공휴일
        "2026-05-05", // 어린이날
        "2026-05-24", "2026-05-25", // 부처님오신날, 대체공휴일
        "2026-06-03", // 전국동시지방선거
        "2026-06-06", // 현충일
        "2026-07-17", // 제헌절
        "2026-08-15", "2026-08-17", // 광복절, 대체공휴일
        "2026-09-24", "2026-09-25", "2026-09-26", // 추석
        "2026-10-03", "2026-10-05", // 개천절, 대체공휴일
        "2026-10-09", // 한글날
        "2026-12-25", // 성탄절
    ).map(LocalDate::parse).toSet()

    fun isRedDay(date: LocalDate): Boolean =
        date.dayOfWeek == DayOfWeek.SATURDAY ||
            date.dayOfWeek == DayOfWeek.SUNDAY ||
            date in HOLIDAYS_2026
}
