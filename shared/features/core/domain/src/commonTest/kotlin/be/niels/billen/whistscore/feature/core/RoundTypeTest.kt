package be.niels.billen.whistscore.feature.core

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class RoundTypeTest : FreeSpec() {

    init {
        "every round type carries a display name and description" {
            RoundType.entries.forEach { type ->
                type.displayName.isNotBlank() shouldBe true
                type.description.isNotBlank() shouldBe true
            }
        }
    }
}
