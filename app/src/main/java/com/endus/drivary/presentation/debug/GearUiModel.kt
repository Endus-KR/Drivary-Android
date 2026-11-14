package com.endus.drivary.presentation.debug

import com.endus.drivary.domain.model.Gear

/** 기어 선택 버튼 순서대로 정의한다 */
enum class GearUiModel(val label: String) {
    P("P"),
    R("R"),
    N("N"),
    D("D"),
}

fun Gear.toUiModel(): GearUiModel = when (this) {
    Gear.PARK, Gear.UNKNOWN -> GearUiModel.P
    Gear.REVERSE -> GearUiModel.R
    Gear.NEUTRAL -> GearUiModel.N
    Gear.DRIVE -> GearUiModel.D
}

fun GearUiModel.toDomain(): Gear = when (this) {
    GearUiModel.P -> Gear.PARK
    GearUiModel.R -> Gear.REVERSE
    GearUiModel.N -> Gear.NEUTRAL
    GearUiModel.D -> Gear.DRIVE
}
