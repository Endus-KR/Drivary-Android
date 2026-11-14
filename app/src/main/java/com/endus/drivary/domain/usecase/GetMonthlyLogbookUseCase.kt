package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Driver
import com.endus.drivary.domain.model.LogbookEntry
import com.endus.drivary.domain.model.LogbookRow
import com.endus.drivary.domain.model.LogbookSummary
import com.endus.drivary.domain.model.MonthlyLogbook
import com.endus.drivary.domain.model.Trip
import com.endus.drivary.domain.model.TripClassification
import com.endus.drivary.domain.model.commuteKm
import com.endus.drivary.domain.model.generalBusinessKm
import com.endus.drivary.domain.repository.DriverRepository
import com.endus.drivary.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.roundToLong

/**
 * 한 달 치 운행을 국세청 운행기록부 줄과 합계로 바꾼다.
 * 운행이 추가되거나 분류가 바뀌면 다시 계산해 흘려보낸다.
 */
class GetMonthlyLogbookUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val driverRepository: DriverRepository,
    private val clock: Clock,
) {

    operator fun invoke(month: YearMonth): Flow<MonthlyLogbook> =
        tripRepository.observeTrips().map { trips ->
            buildMonthlyLogbook(
                month = month,
                trips = trips,
                driver = driverRepository.getDriver(),
                zoneId = clock.zone,
            )
        }
}

/** 운행 목록에서 [month]에 시작한 운행만 골라 운행기록부를 만든다 */
internal fun buildMonthlyLogbook(
    month: YearMonth,
    trips: List<Trip>,
    driver: Driver,
    zoneId: ZoneId,
): MonthlyLogbook {
    val rows = trips
        .filter { YearMonth.from(it.startedAt.atZone(zoneId)) == month }
        .sortedBy { it.startedAt }
        .map { it.toLogbookRow(zoneId) }

    val total = rows.sumOf { it.distanceKm }
    val businessUse = rows.sumOf { it.commuteKm + it.generalBusinessKm }
    return MonthlyLogbook(
        month = month,
        driver = driver,
        rows = rows,
        summary = LogbookSummary(
            totalDistanceKm = total,
            businessUseDistanceKm = businessUse,
            businessUseRatioPercent = ratioPercent(businessUse, total),
        ),
    )
}

private fun Trip.toLogbookRow(zoneId: ZoneId): LogbookRow {
    // 원본 값끼리 빼고 반올림하지 않는다. 반올림한 계기판 값끼리 뺀다
    val before = odometerBeforeKm.roundToLong()
    val after = odometerAfterKm.roundToLong()
    return LogbookRow(
        tripId = id,
        date = startedAt.atZone(zoneId).toLocalDate(),
        odometerBeforeKm = before,
        odometerAfterKm = after,
        distanceKm = after - before,
        entry = when (val c = classification) {
            is TripClassification.Business -> LogbookEntry.Business(c.purpose)
            TripClassification.Personal -> LogbookEntry.Personal
            TripClassification.Unclassified -> LogbookEntry.Unclassified
        },
    )
}

private fun ratioPercent(part: Long, total: Long): Double =
    if (total <= 0L) {
        0.0
    } else {
        BigDecimal.valueOf(part * 100)
            .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP)
            .toDouble()
    }
