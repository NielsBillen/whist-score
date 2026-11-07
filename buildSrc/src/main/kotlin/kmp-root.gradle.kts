plugins {
    id("kmp-kover")
}

kover {
    merge {
        subprojects()
    }
}