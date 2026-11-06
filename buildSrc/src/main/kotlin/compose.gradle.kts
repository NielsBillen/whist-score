plugins {
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

//kotlin {
//    sourceSets {
//        commonMain {
//            dependencies {
//                implementation(libs.compose.runtime)
//                implementation(libs.compose.foundation)
//                implementation(libs.compose.ui)
//                implementation(libs.compose.material3)
//                implementation(libs.compose.components.resources)
//                implementation(libs.compose.uiToolingPreview)
//            }
//        }
//    }
//}