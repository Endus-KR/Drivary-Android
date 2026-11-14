package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Place
import com.endus.drivary.domain.repository.PlaceRepository
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
) {
    suspend operator fun invoke(): Result<List<Place>> = runCatching { placeRepository.getPlaces() }
}
