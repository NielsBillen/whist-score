wayfinder:research

## Question

Determine whether nav3 (`org.jetbrains.androidx.navigation3:navigation3-ui`, current ~1.1.1) is usable in this project's Compose Multiplatform stack, and what it would take to adopt it.

Read the docs: https://kotlinlang.org/docs/multiplatform/compose-navigation-3.html

Find out:
- Current stable version and its required Compose/Kotlin/AGP BOM (`cmpNavigation3`) coordinates — confirm the user's `1.1.1` and whether a newer version exists.
- Whether navigation3 UI supports the targets this app uses (JVM/Desktop, Android, iOS/native, WasmJs). This is the big risk: navigation3 was originally desktop/JVM-focused.
- How screen/destination state maps to the existing `AppScreen` enum in `presentation/app/AppAction.kt` and whether it can replace the current `AnimatedContent`-based `AddRoundView` dispatch.
- Whether this app is currently using Koin for DI (it is) — confirm navigation3 doesn't impose a framework that conflicts.

Output: a short recommendation — adopt nav3, or recommend an alternative — with concrete coordinate versions and any target-compatibility caveats.
