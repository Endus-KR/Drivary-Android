package com.endus.drivary.data.mapper

import ai.pleos.playground.vehicle.constant.control.VehicleGear
import com.endus.drivary.domain.model.Gear

/** Pleos 기어 값 → 도메인 기어. 수동 단수(GEAR_1~9)는 전진이므로 D로 본다 */
fun VehicleGear?.toDomain(): Gear = when (this) {
    VehicleGear.GEAR_PARK -> Gear.PARK
    VehicleGear.GEAR_REVERSE -> Gear.REVERSE
    VehicleGear.GEAR_NEUTRAL -> Gear.NEUTRAL
    VehicleGear.GEAR_DRIVE,
    VehicleGear.GEAR_1, VehicleGear.GEAR_2, VehicleGear.GEAR_3,
    VehicleGear.GEAR_4, VehicleGear.GEAR_5, VehicleGear.GEAR_6,
    VehicleGear.GEAR_7, VehicleGear.GEAR_8, VehicleGear.GEAR_9,
    -> Gear.DRIVE

    VehicleGear.GEAR_UNKNOWN, null -> Gear.UNKNOWN
}
