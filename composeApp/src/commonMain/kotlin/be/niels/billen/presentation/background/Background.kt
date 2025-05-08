package be.niels.billen.presentation.background

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import be.niels.billen.presentation.theme.Icons
import be.niels.billen.presentation.theme.icons.Clubs
import be.niels.billen.presentation.theme.icons.Diamonds
import be.niels.billen.presentation.theme.icons.Hearts
import be.niels.billen.presentation.theme.icons.Spades
import kotlin.math.ceil
import kotlin.math.max

private val images = listOf(
    Icons.Clubs,
    Icons.Hearts,
    Icons.Diamonds,
    Icons.Spades,
)

const val scale = 0.3f

@Composable
fun Background(modifier: Modifier = Modifier, tint: Color = Color(0xFF1a1a1f)) {
    val painters = images.map { rememberVectorPainter(it) }
    val colorFilter = ColorFilter.tint(tint)

    Canvas(modifier) {
        val maxSize = painters.map {it.intrinsicSize }.maxOf { max(it.width, it.height) } * scale
        val spacing = 0.25f * maxSize
        val offset = (0.1f * maxSize).mod(maxSize)
        val horizontalCount = ceil((size.width + spacing) / (maxSize + spacing)).toInt()
        val verticalCount = ceil((size.height + spacing) / (maxSize + spacing)).toInt()

        for (j in -1..verticalCount) {
            val indent = (j % 2) * (maxSize + spacing) * 0.5f
            val y = j * (maxSize + spacing) + maxSize * 0.5f + offset
            for (i in 0..horizontalCount) {
                val x = i * (maxSize + spacing) + maxSize * 0.5f - indent + offset
                val painter = painters[(i + 2 * j).mod(painters.size)]
                drawCentered(Offset(x, y), painter, scale, colorFilter)
            }
        }
    }
}

private fun DrawScope.drawCentered(offset: Offset, painter: VectorPainter, scale: Float, colorFilter: ColorFilter) {
    val imageSize = painter.intrinsicSize * scale

    translate(offset.x - imageSize.width * 0.5f, offset.y - imageSize.height * 0.5f) {
        drawIntoCanvas { canvas ->
            with(painter) {
                draw(size = painter.intrinsicSize * scale, colorFilter = colorFilter)
            }
        }
    }
}