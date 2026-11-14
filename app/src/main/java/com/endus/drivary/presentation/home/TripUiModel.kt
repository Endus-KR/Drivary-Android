package com.endus.drivary.presentation.home

import com.endus.drivary.domain.model.Trip
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class TripUiModel(
    val id: Long,
    val dateText: String,
    val timeRangeText: String,
    val odometerText: String,
    val distanceText: String,
)

private val dateFormatter = DateTimeFormatter.ofPattern("M/d(E)", Locale.KOREAN)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.KOREAN)
private val kmFormat = DecimalFormat("#,##0.0")

fun Trip.toUiModel(zoneId: ZoneId = ZoneId.systemDefault()): TripUiModel {
    val start = startedAt.atZone(zoneId)
    val end = endedAt.atZone(zoneId)
    return TripUiModel(
        id = id,
        dateText = start.format(dateFormatter),
        timeRangeText = "${start.format(timeFormatter)} ~ ${end.format(timeFormatter)}",
        odometerText = "${kmFormat.format(odometerBeforeKm)} → ${kmFormat.format(odometerAfterKm)} km",
        distanceText = "${kmFormat.format(distanceKm)} km",
    )
}
