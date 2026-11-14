package com.endus.drivary.domain.model

import java.time.ZoneId

/**
 * 앱의 기준 시간대. 국세청 운행기록부·공휴일 판단이 한국 날짜 기준이라 기기 설정과 무관하게 서울로 고정한다.
 * (Pleos 에뮬레이터는 기본 시간대가 한국이 아니고, 설정에서 바꿀 수도 없다)
 */
val DRIVARY_ZONE: ZoneId = ZoneId.of("Asia/Seoul")
