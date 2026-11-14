package com.endus.drivary.presentation.home

import com.endus.drivary.domain.model.PendingTripCard
import com.endus.drivary.domain.model.TripPurpose
import java.text.DecimalFormat

data class PendingTripCardUiModel(
    val tripId: Long,
    /** "본사에 출퇴근하러 방문하셨나요?" 또는 "미래오토 정비소 방문" */
    val title: String,
    val distanceText: String,
    val dateText: String,
    val isRedDay: Boolean,
    val suggestedPurpose: PurposeUiModel?,
    val pendingCountText: String,
)

data class PurposeUiModel(
    val id: String,
    val label: String,
) {
    companion object {
        val ALL: List<PurposeUiModel> = TripPurpose.entries.map { it.toUiModel() }
    }
}

fun TripPurpose.toUiModel(): PurposeUiModel = PurposeUiModel(id = name, label = displayName)

fun PurposeUiModel.toDomain(): TripPurpose = TripPurpose.valueOf(id)

private val distanceFormat = DecimalFormat("#,##0.#")

fun PendingTripCard.toUiModel(): PendingTripCardUiModel {
    val placeName = trip.destination?.name ?: "알 수 없는 장소"
    val purpose = suggestedPurpose
    val tripUiModel = trip.toUiModel()
    return PendingTripCardUiModel(
        tripId = trip.id,
        title = if (purpose != null) {
            "${placeName}에 ${purpose.displayName}하러 방문하셨나요?"
        } else {
            "$placeName 방문"
        },
        distanceText = "${distanceFormat.format(trip.distanceKm)}km",
        dateText = "${tripUiModel.dateText}  ${tripUiModel.timeRangeText}",
        isRedDay = isRedDay,
        suggestedPurpose = purpose?.toUiModel(),
        pendingCountText = "미처리 ${pendingCount}건",
    )
}
