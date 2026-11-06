plugins {
    alias(libs.plugins.kover)
}

kover {
    reports {
        filters {
            excludes {
                // *Kt = file-facade classes (top-level declarations, e.g. composables).
                classes("*Kt")
            }
        }
    }
}