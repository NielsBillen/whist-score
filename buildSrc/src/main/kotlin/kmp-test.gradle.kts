plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
        }

        // The jvm target is declared later, by the kmp script; match lazily.
        matching { it.name == "jvmTest" }.configureEach {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
