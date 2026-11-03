package be.niels.billen.whistScore.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import be.niels.billen.whistscore.feature.ui.UiTheme
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull

@OptIn(ExperimentalTestApi::class)
class UiThemeTest : FreeSpec() {

    init {
        "ui theme applies a two-weight custom family to titleLarge" {
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
            fonts.shouldHaveSize(2)
        }
    }
}
