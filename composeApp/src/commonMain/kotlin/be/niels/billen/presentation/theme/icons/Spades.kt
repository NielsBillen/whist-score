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

public val Icons.Spades: ImageVector
    get() {
        if (_spades != null) {
            return _spades!!
        }
        _spades = Builder(name = "Spades", defaultWidth = 368.29.dp, defaultHeight = 393.73.dp,
                viewportWidth = 97.44f, viewportHeight = 104.17f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.420054f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(50.37f, 0.43f)
                curveToRelative(-1.02f, -0.58f, -2.27f, -0.58f, -3.29f, 0.0f)
                curveTo(45.15f, 1.51f, 0.0f, 27.26f, 0.0f, 58.75f)
                curveToRelative(0.0f, 15.52f, 12.67f, 28.14f, 28.24f, 28.14f)
                curveToRelative(3.14f, 0.0f, 6.18f, -0.5f, 9.07f, -1.48f)
                curveToRelative(-2.29f, 4.79f, -5.31f, 9.23f, -8.95f, 13.11f)
                curveToRelative(-0.92f, 0.98f, -1.17f, 2.41f, -0.63f, 3.63f)
                curveToRelative(0.53f, 1.23f, 1.74f, 2.03f, 3.08f, 2.03f)
                horizontalLineToRelative(36.1f)
                curveToRelative(1.34f, 0.0f, 2.55f, -0.8f, 3.08f, -2.03f)
                curveToRelative(0.53f, -1.23f, 0.28f, -2.66f, -0.63f, -3.63f)
                curveToRelative(-3.61f, -3.85f, -6.62f, -8.26f, -8.9f, -13.01f)
                curveToRelative(2.8f, 0.91f, 5.74f, 1.38f, 8.76f, 1.38f)
                curveToRelative(15.57f, 0.0f, 28.24f, -12.62f, 28.24f, -28.14f)
                curveTo(97.44f, 27.26f, 52.29f, 1.51f, 50.37f, 0.43f)
                close()
                moveTo(69.21f, 80.16f)
                curveToRelative(-4.74f, 0.0f, -9.23f, -1.51f, -13.01f, -4.38f)
                curveToRelative(-1.15f, -0.88f, -2.74f, -0.91f, -3.93f, -0.09f)
                curveToRelative(-1.19f, 0.82f, -1.73f, 2.31f, -1.33f, 3.7f)
                curveToRelative(1.87f, 6.47f, 4.88f, 12.6f, 8.84f, 18.05f)
                horizontalLineTo(37.93f)
                curveToRelative(4.01f, -5.53f, 7.04f, -11.75f, 8.91f, -18.32f)
                curveToRelative(0.4f, -1.4f, -0.15f, -2.9f, -1.36f, -3.71f)
                curveToRelative(-1.21f, -0.81f, -2.8f, -0.75f, -3.95f, 0.15f)
                curveToRelative(-3.82f, 3.0f, -8.42f, 4.59f, -13.29f, 4.59f)
                curveToRelative(-11.86f, 0.0f, -21.52f, -9.61f, -21.52f, -21.42f)
                curveToRelative(0.0f, -14.16f, 11.67f, -27.56f, 21.47f, -36.31f)
                curveToRelative(8.46f, -7.56f, 17.03f, -13.04f, 20.53f, -15.17f)
                curveToRelative(3.51f, 2.13f, 12.08f, 7.62f, 20.53f, 15.17f)
                curveToRelative(17.74f, 15.85f, 21.47f, 28.49f, 21.47f, 36.31f)
                curveToRelative(0.0f, 11.81f, -9.65f, 21.42f, -21.51f, 21.42f)
                close()
            }
        }
        .build()
        return _spades!!
    }

private var _spades: ImageVector? = null
