package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.LogbookRow
import com.endus.drivary.domain.model.MonthlyLogbook
import com.endus.drivary.domain.model.commuteKm
import com.endus.drivary.domain.model.generalBusinessKm
import com.endus.drivary.domain.model.note
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/** 내보낼 운행기록부 CSV 파일 */
data class LogbookCsv(
    val fileName: String,
    val content: String,
)

/** 미처리 운행이 남아 있어 내보낼 수 없을 때 */
class UnclassifiedTripsRemainException(val count: Int) :
    IllegalStateException("미처리 운행 ${count}건이 남아 있어 내보낼 수 없습니다")

/**
 * 운행기록부를 국세청 서식 칸 순서의 CSV로 만든다.
 * 엑셀에서 한글이 깨지지 않게 UTF-8 BOM을 붙인다. 미처리 운행이 남아 있으면 만들지 않는다.
 */
class BuildLogbookCsvUseCase @Inject constructor() {

    operator fun invoke(logbook: MonthlyLogbook): Result<LogbookCsv> = runCatching {
        if (logbook.unclassifiedCount > 0) throw UnclassifiedTripsRemainException(logbook.unclassifiedCount)

        val driver = logbook.driver
        val lines = buildList {
            add(HEADER)
            logbook.rows.forEach { row -> add(row.toCsvCells(driver.department, driver.name)) }
            add(emptyList())
            add(listOf("총주행거리(km)", logbook.summary.totalDistanceKm.toString()))
            add(listOf("업무용 사용거리(km)", logbook.summary.businessUseDistanceKm.toString()))
            add(listOf("업무사용비율(%)", "%.1f".format(Locale.ROOT, logbook.summary.businessUseRatioPercent)))
        }
        LogbookCsv(
            fileName = "운행기록부_${logbook.month}.csv",
            content = BOM + lines.joinToString(LINE_SEPARATOR) { cells -> cells.joinToString(",") { it.escapeCsv() } },
        )
    }

    private fun LogbookRow.toCsvCells(department: String, name: String): List<String> = listOf(
        date.format(DATE_FORMATTER),
        department,
        name,
        odometerBeforeKm.toString(),
        odometerAfterKm.toString(),
        distanceKm.toString(),
        commuteKm.toString(),
        generalBusinessKm.toString(),
        note,
    )

    private fun String.escapeCsv(): String =
        if (any { it == ',' || it == '"' || it == '\n' || it == '\r' }) "\"${replace("\"", "\"\"")}\"" else this

    private companion object {
        const val BOM = "﻿"
        const val LINE_SEPARATOR = "\r\n"
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d(E)", Locale.KOREAN)
        val HEADER = listOf(
            "사용일자(요일)", "부서", "성명",
            "주행 전 계기판의 거리(km)", "주행 후 계기판의 거리(km)", "주행거리(km)",
            "출·퇴근용(km)", "일반 업무용(km)", "비고",
        )
    }
}
