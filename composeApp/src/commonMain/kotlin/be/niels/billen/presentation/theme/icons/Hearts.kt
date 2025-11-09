package be.niels.billen.presentation.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import be.niels.billen.presentation.theme.Icons

val Icons.Hearts by lazy {
    Builder(
        name = "Hearts", defaultWidth = 380.72.dp, defaultHeight = 355.62.dp,
        viewportWidth = 100.73f, viewportHeight = 94.09f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.420054f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(71.59f, 0.0f)
            curveTo(63.49f, 0.0f, 55.83f, 3.51f, 50.37f, 9.58f)
            curveTo(44.9f, 3.51f, 37.24f, 0.0f, 29.14f, 0.0f)
            curveTo(13.07f, 0.0f, 0.0f, 13.62f, 0.0f, 30.37f)
            curveToRelative(0.0f, 14.47f, 8.26f, 29.56f, 24.56f, 44.85f)
            curveToRelative(11.88f, 11.15f, 23.6f, 18.12f, 24.09f, 18.41f)
            curveToRelative(0.53f, 0.31f, 1.12f, 0.47f, 1.71f, 0.47f)
            curveToRelative(0.59f, 0.0f, 1.18f, -0.16f, 1.71f, -0.47f)
            curveTo(52.57f, 93.33f, 64.28f, 86.36f, 76.17f, 75.21f)
            curveTo(92.47f, 59.93f, 100.73f, 44.84f, 100.73f, 30.37f)
            curveTo(100.73f, 13.62f, 87.66f, 0.0f, 71.59f, 0.0f)
            close()
            moveTo(50.37f, 86.77f)
            curveTo(42.09f, 81.47f, 6.72f, 57.22f, 6.72f, 30.37f)
            curveToRelative(0.0f, -13.04f, 10.06f, -23.65f, 22.42f, -23.65f)
            curveToRelative(7.34f, 0.0f, 14.22f, 3.81f, 18.42f, 10.19f)
            curveToRelative(0.62f, 0.94f, 1.68f, 1.51f, 2.81f, 1.51f)
            curveToRelative(1.13f, 0.0f, 2.19f, -0.57f, 2.81f, -1.51f)
            curveToRelative(4.2f, -6.38f, 11.09f, -10.19f, 18.42f, -10.19f)
            curveToRelative(12.36f, 0.0f, 22.42f, 10.61f, 22.42f, 23.65f)
            curveToRelative(0.0f, 26.86f, -35.37f, 51.1f, -43.64f, 56.4f)
            close()
        }
    }
        .build()
}

