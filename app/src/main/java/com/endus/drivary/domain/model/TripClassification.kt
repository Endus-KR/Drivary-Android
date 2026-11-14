package com.endus.drivary.domain.model

/** 운행 분류. 1단계 업무/개인, 2단계 업무 목적(업무일 때만) */
sealed interface TripClassification {

    /** 아직 분류하지 않은 운행. 미처리 카드로 묻는다 */
    data object Unclassified : TripClassification

    /** 업무 운행 */
    data class Business(val purpose: TripPurpose) : TripClassification

    /** 개인(비업무) 운행. 목적은 저장하지 않는다 */
    data object Personal : TripClassification
}
