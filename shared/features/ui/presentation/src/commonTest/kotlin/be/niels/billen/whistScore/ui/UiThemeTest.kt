package be.niels.billen.whistScore.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import be.niels.billen.whistscore.feature.ui.Style
import be.niels.billen.whistscore.feature.ui.UiTheme
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class UiThemeTest : FreeSpec() {

    init {
        "ui theme applies a three-weight custom family to titleLarge" {
            var titleFontFamily: FontFamily? = null
            runSkikoComposeUiTest {
                setContent {
                    UiTheme {
                        titleFontFamily = MaterialTheme.typography.titleLarge.fontFamily
                    }
                }
            }
            titleFontFamily.shouldNotBeNull()
            val fonts = (titleFontFamily as? FontListFontFamily)?.fonts ?: emptyList()
            fonts.shouldHaveSize(3)
        }

        "ui theme maps onSurface to the y2k primary text color" {
            var onSurface: Color? = null
            runSkikoComposeUiTest {
                setContent {
                    UiTheme {
                        onSurface = MaterialTheme.colorScheme.onSurface
                    }
                }
            }
            onSurface shouldBe Style.Colors.textPrimary
        }

        "ui theme provides a non-black default content color on the dark scheme" {
            var contentColor: Color? = null
            runSkikoComposeUiTest {
                setContent {
                    UiTheme {
                        contentColor = LocalContentColor.current
                    }
                }
            }
            contentColor shouldBe Style.Colors.textPrimary
            contentColor shouldNotBe Color.Black
        }
    }
}
