package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Trip
import com.endus.drivary.domain.model.TripClassification
import com.endus.drivary.domain.model.TripPurpose
import com.endus.drivary.domain.repository.PlaceRepository
import com.endus.drivary.domain.repository.TripRepository
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * 이번 달 샘플 운행 5건을 넣는다 (디버그용).
 * 업무(출퇴근·납품·영업), 개인, 미처리를 고루 섞어 분류 카드·운행기록부를 바로 확인할 수 있게 한다.
 */
class AddSampleTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val placeRepository: PlaceRepository,
    private val clock: Clock,
) {

    suspend operator fun invoke(): Result<List<Trip>> = runCatching {
        val places = placeRepository.getPlaces().associateBy { it.name }
        val firstDay = LocalDate.now(clock).withDayOfMonth(1)
        val samples = listOf(
            Sample(day = 3, start = LocalTime.of(8, 20), distanceKm = 12.3, placeName = "본사",
                classification = TripClassification.Business(TripPurpose.COMMUTE)),
            Sample(day = 3, start = LocalTime.of(10, 5), distanceKm = 38.6, placeName = "한빛물류센터",
                classification = TripClassification.Business(TripPurpose.DELIVERY)),
            Sample(day = 4, start = LocalTime.of(14, 40), distanceKm = 21.4, placeName = "대성부품 대리점",
                classification = TripClassification.Business(TripPurpose.SALES)),
            Sample(day = 6, start = LocalTime.of(11, 0), distanceKm = 5.2, placeName = "동네 마트",
                classification = TripClassification.Personal),
            Sample(day = 7, start = LocalTime.of(9, 15), distanceKm = 15.7, placeName = "미래오토 정비소",
                classification = TripClassification.Unclassified),
        )

        var odometerKm = SAMPLE_START_ODOMETER_KM
        samples.map { sample ->
            val startedAt = ZonedDateTime.of(
                firstDay.plusDays(sample.day - 1L),
                sample.start,
                clock.zone,
            )
            // 시속 약 40km로 달렸다고 보고 종료 시각을 만든다
            val minutes = (sample.distanceKm / 40.0 * 60).toLong().coerceAtLeast(1)
            val trip = Trip(
                startedAt = startedAt.toInstant(),
                endedAt = startedAt.plusMinutes(minutes).toInstant(),
                odometerBeforeKm = odometerKm,
                odometerAfterKm = odometerKm + sample.distanceKm,
                destination = places[sample.placeName],
                classification = sample.classification,
            )
            odometerKm += sample.distanceKm
            tripRepository.addTrip(trip)
        }
    }

    private data class Sample(
        val day: Int,
        val start: LocalTime,
        val distanceKm: Double,
        val placeName: String,
        val classification: TripClassification,
    )

    private companion object {
        const val SAMPLE_START_ODOMETER_KM = 12_000.4
    }
}
