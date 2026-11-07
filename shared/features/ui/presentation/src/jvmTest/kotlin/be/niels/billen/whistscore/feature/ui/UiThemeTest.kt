package be.niels.billen.whistscore.feature.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@OptIn(ExperimentalTestApi::class)
class UiThemeTest : FreeSpec() {
    init {
        "ui theme applies a three-weight custom family to titleLarge" {
            var titleFontFamily: FontFamily? = null
            runComposeUiTest {
                setContent {
                    UiTheme {
                        titleFontFamily = MaterialTheme.typography.titleLarge.fontFamily
                    }
                }
            }
            titleFontFamily.shouldNotBeNull()
            val fonts = (titleFontFamily as? FontListFontFamily)?.fonts.orEmpty()
            fonts.shouldHaveSize(3)
        }

        "ui theme maps onSurface to the y2k primary text color" {
            var onSurface: Color? = null
            runComposeUiTest {
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
            runComposeUiTest {
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
