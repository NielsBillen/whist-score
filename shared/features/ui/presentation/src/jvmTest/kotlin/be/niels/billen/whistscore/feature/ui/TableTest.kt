package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import be.niels.billen.whistscore.feature.core.icons.Trash
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalTestApi::class)
class TableTest : FreeSpec() {

    private val rows: ImmutableList<String> = persistentListOf("alpha", "beta", "gamma")

    init {
        "renders the header slot and one row per item" {
            withTable(rows = rows) {
                onNodeWithText("HEADER").assertIsDisplayed()
                onNodeWithText("alpha 0").assertIsDisplayed()
                onNodeWithText("beta 1").assertIsDisplayed()
                onNodeWithText("gamma 2").assertIsDisplayed()
            }
        }

        "row slots receive the item and its index" {
            withTable(rows = rows) {
                onNodeWithText("alpha 0").assertIsDisplayed()
                onNodeWithText("gamma 2").assertIsDisplayed()
            }
        }

        "row slot content can fire callbacks with the row's index" {
            var deletedIndex = -1
            withTable(rows = rows, onDelete = { deletedIndex = it }) {
                onNodeWithContentDescription("Delete row 1").performClick()
            }
            deletedIndex shouldBe 1
        }
    }
}

@OptIn(ExperimentalTestApi::class)
private fun withTable(
    rows: ImmutableList<String>,
    onDelete: ((Int) -> Unit)? = null,
    block: suspend ComposeUiTest.() -> Unit,
) {
    runSkikoComposeUiTest {
        setContent {
            MaterialTheme {
                Table(
                    rows = rows,
                    header = {
                        Text(text = "HEADER")
                    },
                    rowContent = { item, index ->
                        Row {
                            Text(text = "$item $index", modifier = Modifier.weight(1f))

                            if (onDelete != null) {
                                IconButton(
                                    onClick = { onDelete(index) },
                                    modifier = Modifier.requiredSize(32.dp),
                                ) {
                                    Icon(
                                        imageVector = Trash,
                                        contentDescription = "Delete row $index"
                                    )
                                }
                            }
                        }
                    },
                )
            }
        }
        block()
    }
}
