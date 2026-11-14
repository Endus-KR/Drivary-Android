package com.endus.drivary.presentation.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.endus.drivary.presentation.designsystem.theme.DrivaryStatusColors

/** 운행 상태. 색만 봐도 구분되게 한다 */
enum class DrivaryTagStyle { Business, Personal, Unclassified, RedDay }

/** 업무·개인·미처리·빨간날 표시용 작은 꼬리표 */
@Composable
fun DrivaryTag(
    text: String,
    style: DrivaryTagStyle,
    modifier: Modifier = Modifier,
) {
    val color = when (style) {
        DrivaryTagStyle.Business -> DrivaryStatusColors.Business
        DrivaryTagStyle.Personal -> DrivaryStatusColors.Personal
        DrivaryTagStyle.Unclassified -> DrivaryStatusColors.Unclassified
        DrivaryTagStyle.RedDay -> DrivaryStatusColors.RedDay
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        maxLines = 1,
        modifier = modifier
            .background(color.copy(alpha = 0.16f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}
