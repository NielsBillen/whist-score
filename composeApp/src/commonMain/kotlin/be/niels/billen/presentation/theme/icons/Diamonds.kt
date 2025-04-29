package be.niels.billen.presentation.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import be.niels.billen.presentation.theme.Icons

public val Icons.Diamonds: ImageVector
    get() {
        if (_diamonds != null) {
            return _diamonds!!
        }
        _diamonds = Builder(name = "Diamonds", defaultWidth = 381.03.dp, defaultHeight = 381.03.dp,
                viewportWidth = 100.81f, viewportHeight = 100.81f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.420054f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(1.38f, 53.12f)
                curveToRelative(15.28f, 11.12f, 35.19f, 31.02f, 46.31f, 46.3f)
                curveToRelative(0.63f, 0.87f, 1.64f, 1.38f, 2.72f, 1.38f)
                curveToRelative(1.08f, 0.0f, 2.09f, -0.51f, 2.72f, -1.38f)
                curveToRelative(11.12f, -15.28f, 31.02f, -35.19f, 46.31f, -46.3f)
                curveToRelative(0.87f, -0.63f, 1.38f, -1.64f, 1.38f, -2.72f)
                curveToRelative(0.0f, -1.08f, -0.51f, -2.09f, -1.38f, -2.72f)
                curveTo(84.15f, 36.57f, 64.24f, 16.67f, 53.12f, 1.38f)
                curveTo(52.49f, 0.51f, 51.48f, 0.0f, 50.41f, 0.0f)
                curveTo(49.33f, 0.0f, 48.32f, 0.51f, 47.69f, 1.38f)
                curveTo(36.57f, 16.67f, 16.67f, 36.57f, 1.38f, 47.69f)
                curveTo(0.51f, 48.32f, 0.0f, 49.33f, 0.0f, 50.41f)
                curveToRelative(0.0f, 1.08f, 0.51f, 2.09f, 1.38f, 2.72f)
                close()
                moveTo(50.41f, 8.95f)
                curveToRelative(10.94f, 14.12f, 27.34f, 30.51f, 41.45f, 41.45f)
                curveToRelative(-14.12f, 10.94f, -30.51f, 27.34f, -41.45f, 41.45f)
                curveToRelative(-10.94f, -14.12f, -27.34f, -30.51f, -41.45f, -41.45f)
                curveToRelative(14.12f, -10.94f, 30.51f, -27.34f, 41.45f, -41.45f)
                close()
            }
        }
        .build()
        return _diamonds!!
    }

private var _diamonds: ImageVector? = null
