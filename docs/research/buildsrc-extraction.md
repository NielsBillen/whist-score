# Extracting the shared KMP build logic into precompiled script plugins

Design for moving the duplicated KMP target / Compose / test wiring out of the 14 shared
module build files (`shared/` root + the 13 feature modules under
`shared/features/**/{domain,data,presentation}`) into three precompiled script plugins in
`buildSrc/src/main/kotlin/`: `kotlin.kts`, `compose.kts`, `test.kts`.

Grounded in [buildsrc.md](buildsrc.md): the scripts get bare plugin IDs (versions come from
`buildSrc`'s implementation classpath), library lookups go through the
`versionCatalogs` extension (type-safe `libs` accessors do not resolve in precompiled
scripts), and `projects.*` accessors are unavailable — so the root
`build.gradle.kts` keeps its `allWarningsAsErrors` `subprojects` block, the
`kover(projects.*)` wiring, the Kover report filters, and the detekt config.

The app modules (`androidApp`, `desktopApp`, `webApp`) and the root build file stay
untouched in scope: the scripts are applied **only** from the 14 shared modules.

## 1. Script boundaries

| Script | Applies plugins | Configures |
|---|---|---|
| `kotlin` | `org.jetbrains.kotlin.multiplatform`, `com.android.kotlin.multiplatform.library`, `org.jetbrains.kotlin.plugin.serialization` | KMP targets (`android` + `jvm` + `wasmJs/nodejs`), `jvmTarget = 11` on the android target, the `kotlin.js.ExperimentalWasmJsInterop` opt-in, the commented-out iOS target block |
| `compose` | `org.jetbrains.compose`, `org.jetbrains.kotlin.plugin.compose` | the six shared Compose `commonMain` dependencies |
| `test` | `io.kotest` | the shared `commonTest` / `jvmTest` test dependencies, `useJUnitPlatform()`, and — only in Compose modules — the JVM-only `compose.desktop.currentOs` jvmTest dependency |

`kotlin.kts` and `compose.kts` are applied to **all 14** shared modules.
`test.kts` is applied only to the modules that have tests — the 6 modules with
`commonTest` sources, plus `players/domain` (see §5, risk 1).

Each script applies its plugins with bare `id(...)` in its own `plugins {}` block; a
module's `plugins {}` block becomes a bare `id("kotlin")` / `id("compose")` / `id("test")`
line per script it uses. Plugin **versions** come from `buildSrc/build.gradle.kts`
implementation dependencies, pinned by hand to `gradle/libs.versions.toml` (Kotlin 2.4.21,
AGP 9.3.3, Compose Multiplatform 1.12.2, serialization 2.4.21, kotest 6.2.6) — see
buildsrc.md §4 for the single-source option (import the same toml into
`buildSrc/settings.gradle.kts`).

## 2. The three scripts — exact code sketches

### 2.1 `kotlin.kts`

```kotlin
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val versions = versionCatalogs.named("libs").versions

kotlin {
    android {
        // namespace is set by the consuming module.
        compileSdk = versions.get("android-compileSdk").get().toInt()
        minSdk = versions.get("android-minSdk").get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
    }

    // (iOS target stays commented out; lives here now because every module had the same block.)
    // listOf(
    //     iosArm64(),
    //     iosSimulatorArm64()
    // ).forEach { iosTarget ->
    //     iosTarget.binaries.framework {
    //         baseName = "Shared"
    //         isStatic = true
    //     }
    // }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        compilerOptions { optIn.add("kotlin.js.ExperimentalWasmJsInterop") }
    }
}
```

Parameterization:

- **`namespace`** — not set here; each module keeps
  `kotlin { android { namespace = "…" } }` in its own file (the value differs per module;
  the KMP extension is idempotent, so the module's block merges with the script's).
- **The wasm `testTask { enabled = false }` variant** — not parameterized:
  `ui/presentation` and `addround/presentation` add it in their own `kotlin { wasmJs {
  nodejs { testTask { enabled = false } } } }` block (the `nodejs` extension is already
  created by the script's `nodejs()` call; the module block just configures
  `testTask`). The two-line comment explaining the skiko-wasm/Node limit moves with it.

### 2.2 `compose.kts`

```kotlin
plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = versionCatalogs.named("libs")
val sharedComposeDeps = listOf(
    "compose-runtime",
    "compose-foundation",
    "compose-ui",
    "compose-material3",
    "compose-components-resources",
    "compose-uiToolingPreview",
)

kotlin.sourceSets.named("commonMain") {
    dependencies {
        sharedComposeDeps.forEach {
            implementation(libs.findLibrary(it).get())
        }
    }
}
```

`compose-kotlin.kts`-style `compile()` wiring is **not** needed: Compose Multiplatform
applies the compiler plugin to every target automatically; the repo never used
`compile()`.

### 2.3 `test.kts`

```kotlin
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTest

plugins {
    id("io.kotest")
}

val libs = versionCatalogs.named("libs")

fun jvmTest() = kotlin.sourceSets.named("jvmTest") as JvmTest

kotlin.sourceSets.named("commonTest") {
    dependencies {
        implementation(libs.findLibrary("kotest-framework-engine").get())
        implementation(libs.findLibrary("kotest-assertions-core").get())
    }
}

// JVM-only artifacts: commonTest would resolve them against the wasm test classpath.
jvmTest().dependencies {
    implementation(libs.findLibrary("kotest-runner-junit5").get())
    // currentOs is a JVM artifact, so it lives only in the jvmTest source set;
    // it is added for modules that apply this script from a Compose module.
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
```

The Compose modules need one more jvmTest dependency — `compose.desktop.currentOs`,
which is a JVM artifact. In a normal module build script it is reached through the
type-safe `compose` accessor; in a precompiled script that accessor does not exist, so
`test.kts` reaches the extension by type:

```kotlin
val compose = extensions.findByType(ComposeExtension::class.java)
if (compose != null) {
    jvmTest().dependencies {
        implementation(compose.desktop.currentOs)
    }
}
```

`findByType` (not `getByType`) is deliberate: `test.kts` is applied from
`core/domain` and `game/domain`, which are not Compose modules, and must not fail on
the missing extension. `compose` is in scope only where the script is applied from a
Compose module (see §3 for the per-module split).

## 3. Per-module after-state

What each module's `build.gradle.kts` contains after extraction. The common blocks
(targets, the six Compose deps, the test deps, `useJUnitPlatform`) are gone; only what
differs stays.

### 3.1 `:shared`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")` |
| kotlin | `kotlin { android { namespace = "be.niels.billen.shared" } }` — that's all |
| other | nothing (the module has no source sets or dependencies today) |

### 3.2 `:shared:features:core:domain`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("test")`, `alias(libs.plugins.kover)` |
| kotlin | namespace only |
| sourceSets | `commonMain`: `compose-runtime` and `kotlinx-collections-immutable` — **neither is extracted**: the module applies no Compose plugin, so `compose-runtime` is not in the extracted compose set (see §5 risk 4) |
| other | nothing (test deps and `useJUnitPlatform` now come from `test`) |

### 3.3 `:shared:features:core:presentation`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")`, `alias(libs.plugins.svg2compose)` |
| kotlin | namespace only |
| sourceSets | `commonMain`: only `implementation(libs.kotlinx.collections.immutable)` (the six compose deps are gone) |
| other | the `svgToCompose { processor { … } }` block unchanged, plus the
  `tasks.withType<KspAATask>().configureEach { dependsOn(parseSvgToComposeIcon) }`
  wiring and its comment, and the `KspAATask` import |

### 3.4 `:shared:features:ui:presentation`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")`, `id("test")`, `alias(libs.plugins.kover)` |
| kotlin | namespace, plus the wasm variant (see below) |
| sourceSets | `commonMain`: `androidx-lifecycle-runtime-compose`, `androidx-lifecycle-viewmodel`, `androidx-nav3-ui`, `koin-compose`, `koin-compose-viewmodel`, `kotlinx-collections-immutable`, `project(":shared:features:core:presentation")` |
| other | nothing — commonTest deps and `useJUnitPlatform` come from `test` |

Wasm variant (replaces the plain `nodejs()` line; comment moves with it):

```kotlin
wasmJs {
    // Compose's skiko wasm runtime cannot load under Node, so node tests can't run
    // in this module; the common tests still run on JVM.
    nodejs {
        testTask { enabled = false }
    }
}
```

### 3.5 `:shared:features:app:presentation`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")`, `id("test")`, `alias(libs.plugins.kover)` |
| kotlin | namespace only (plain `nodejs()`, no testTask variant) |
| sourceSets | `commonMain`: `androidx-lifecycle-runtime-compose`, `androidx-lifecycle-viewmodel`, `androidx-lifecycle-viewmodel-navigation3`, `androidx-nav3-ui`, `koin-compose`, `koin-compose-viewmodel`, and the 7 project deps (`addround/presentation`, `core/domain`, `core/presentation`, `editplayers/presentation`, `game/domain`, `overview/presentation`, `ui/presentation`) |
| commonTest | the 4 project deps (`game/domain`, `players/domain`, `players/presentation`, `rounds/presentation`) — everything else comes from `test` |
| other | nothing |

### 3.6 `:shared:features:overview:presentation`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")` |
| kotlin | namespace only |
| sourceSets | `commonMain`: `androidx-lifecycle-viewmodel`, `androidx-lifecycle-runtime-compose`, `koin-compose`, `koin-compose-viewmodel`, and 6 project deps (`core/domain`, `core/presentation`, `game/domain`, `players/presentation`, `rounds/presentation`, `ui/presentation`) |
| other | nothing (no tests today) |

### 3.7 `:shared:features:players:domain`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("test")` (per §5 risk 1) |
| kotlin | namespace only |
| sourceSets | `commonMain`: `kotlinx-collections-immutable`, `kotlinx-coroutines-core`, project deps `core/domain`, `game/domain` |
| other | nothing — its declared test deps and `useJUnitPlatform` line are replaced by `test` |

### 3.8 `:shared:features:players:data`

| Block | After state |
|---|---|
| plugins | `id("kotlin")` |
| kotlin | namespace only |
| sourceSets | `commonMain`: `kotlinx-collections-immutable`, `kotlinx-coroutines-core`, `kotlinx-serialization-core`, `multiplatform-settings`, `multiplatform-settings-serialization`, `koin`, project deps `players/domain`, `core/domain`, `game/domain`, `game/data` |
| other | nothing (no tests) |

### 3.9 `:shared:features:players:presentation`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")` |
| kotlin | namespace only |
| sourceSets | `commonMain`: `kotlinx-collections-immutable`, `androidx-lifecycle-viewmodel`, `androidx-lifecycle-runtime-compose`, `koin-compose`, `koin-compose-viewmodel`, project deps `core/domain`, `core/presentation`, `ui/presentation`, `players/domain`, `game/domain` |
| other | nothing (no tests) |

### 3.10 `:shared:features:rounds:presentation`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")`, `id("test")` |
| kotlin | namespace only |
| sourceSets | `commonMain`: `androidx-lifecycle-runtime-compose`, `androidx-lifecycle-viewmodel`, `koin-compose`, `koin-compose-viewmodel`, `kotlinx-collections-immutable`, project deps `core/domain`, `core/presentation`, `game/domain`, `players/domain`, `ui/presentation` |
| other | nothing — commonTest deps and `useJUnitPlatform` come from `test` |

### 3.11 `:shared:features:addround:presentation`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")`, `id("test")`, `alias(libs.plugins.kover)` |
| kotlin | namespace, plus the same wasm `testTask { enabled = false }` variant as `ui/presentation` (with the same comment) |
| sourceSets | `commonMain`: `androidx-lifecycle-runtime-compose`, `androidx-lifecycle-viewmodel`, `androidx-nav3-ui`, `koin-compose`, `koin-compose-viewmodel`, `kotlinx-collections-immutable`, project deps `core/domain`, `core/presentation`, `game/domain`, `players/domain`, `ui/presentation` |
| other | nothing |

### 3.12 `:shared:features:game:domain`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("test")`, `alias(libs.plugins.kover)` |
| kotlin | namespace only |
| sourceSets | `commonMain`: `kotlinx-collections-immutable`, `compose-runtime`, `kotlinx-coroutines-core`, project dep `core/domain`; `commonTest`: `kotlinx-collections-immutable` (its only test dep not in the common set) |
| other | nothing |

### 3.13 `:shared:features:game:data`

| Block | After state |
|---|---|
| plugins | `id("kotlin")` |
| kotlin | namespace only |
| sourceSets | `commonMain`: `kotlinx-coroutines-core`, `kotlinx-serialization-core`, `kotlinx-collections-immutable`, project deps `game/domain`, `core/domain`, `multiplatform-settings`, `multiplatform-settings-serialization`, `koin` |
| other | nothing (no tests) |

### 3.14 `:shared:features:editplayers:presentation`

| Block | After state |
|---|---|
| plugins | `id("kotlin")`, `id("compose")` |
| kotlin | namespace only |
| sourceSets | `commonMain`: `kotlinx-collections-immutable`, `androidx-lifecycle-viewmodel`, `androidx-lifecycle-runtime-compose`, `koin-compose`, `koin-compose-viewmodel`, project deps `core/domain`, `core/presentation`, `players/domain`, `game/domain`, `ui/presentation` |
| other | nothing (no tests) |

Plugin matrix at a glance (kotlin = `kotlin.kts`, compose = `compose.kts`, test = `test.kts`,
kover = per-module `alias(libs.plugins.kover)`, s2c = per-module
`alias(libs.plugins.svg2compose)`):

| Module | kotlin | compose | test | kover | s2c |
|---|---|---|---|---|---|
| shared | ✓ | ✓ | | | |
| core/domain | ✓ | | ✓ | ✓ | |
| core/presentation | ✓ | ✓ | | | ✓ |
| ui/presentation | ✓ | ✓ | ✓ | ✓ | |
| app/presentation | ✓ | ✓ | ✓ | ✓ | |
| overview/presentation | ✓ | ✓ | | | |
| players/domain | ✓ | | ✓ | | |
| players/data | ✓ | | | | |
| players/presentation | ✓ | ✓ | | | |
| rounds/presentation | ✓ | ✓ | ✓ | | |
| addround/presentation | ✓ | ✓ | ✓ | ✓ | |
| game/domain | ✓ | | ✓ | ✓ | |
| game/data | ✓ | | | | |
| editplayers/presentation | ✓ | ✓ | | | |

## 4. Catalog lookups the scripts make

Every library alias the scripts resolve through `versionCatalogs.named("libs")`,
so `buildSrc` must not touch them and the implementer knows exactly what to look up:

| Script | Lookups |
|---|---|
| `kotlin.kts` | **versions**: `android-compileSdk`, `android-minSdk` (no library lookups) |
| `compose.kts` | `compose-runtime`, `compose-foundation`, `compose-ui`, `compose-material3`, `compose-components-resources`, `compose-uiToolingPreview` |
| `test.kts` | `kotest-framework-engine`, `kotest-assertions-core`, `kotest-runner-junit5`; plus `compose.desktop.currentOs` via the `ComposeExtension` (no catalog alias — it is `org.jetbrains.compose.desktop:desktop-<os>` resolved by the plugin) |

What the scripts **do not** look up: `kotlinx-coroutines-test`, `turbine`,
`compose-ui-test` (those stay module-specific, §5 risk 2), and `kotlinx-coroutines-core`
(stays in module files).

What `buildSrc/build.gradle.kts` must put on the implementation classpath (versions pinned
to the toml, §1): `org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.21`,
`com.android.tools.build:gradle:9.3.3`, `org.jetbrains.compose:compose-gradle-plugin:1.12.2`
(backs `org.jetbrains.compose`), `org.jetbrains.kotlin:kotlin-serialization:2.4.21`
(backs `org.jetbrains.kotlin.plugin.serialization`),
`io.kotest:kotest-gradle-plugin:6.2.6` (backs `io.kotest`). The Compose compiler plugin
(`org.jetbrains.kotlin.plugin.compose`) is supplied by the Compose Gradle plugin
itself; the serialization and kotest artifacts are the plugin jars per buildsrc.md §4.

## 5. Risks and per-module differences the implementer must handle

1. **`players/domain` — test deps declared, no test sources.** Its build file declares
   `commonTest` (`kotest-framework-engine`, `kotest-assertions-core`) and `jvmTest`
   (`kotest-runner-junit5`) plus `useJUnitPlatform()`, but `src/commonTest` does not
   exist. Decision: **apply `test.kts` to it anyway** — the declared deps are exactly the
   common set, so nothing module-specific is lost, and the module file shrinks to the
   commonMain block. If the constraint is read strictly as "source sets only", the
   alternative is to keep its test block in the module file; the recommendation stands
   and is flagged here so the implementer can push back.

2. **Common-test split (recommendation).** The common test set in `test.kts`:
   `kotest-framework-engine` + `kotest-assertions-core` (commonTest),
   `kotest-runner-junit5` (jvmTest), `useJUnitPlatform()` — and, added for Compose
   modules, `compose.desktop.currentOs` (jvmTest).
   Stays **module-specific**: `compose-ui-test`, `kotlinx-coroutines-test`, `turbine` —
   all three are used **only** in Compose modules' tests (`ui/presentation`,
   `app/presentation`, `rounds/presentation`, `addround/presentation`) and never in
   domain tests, so they do not belong in the common set. `app/presentation` keeps
   `kotlinx-collections-immutable` in its commonTest block, and its 4 test project
   deps stay as-is. `game/domain` keeps `kotlinx-collections-immutable` in commonTest.
   Result: after extraction, `ui/presentation`, `rounds/presentation` and
   `addround/presentation` gain a `commonTest` block containing exactly those three
   deps; `app/presentation` gains one containing those three plus
   `kotlinx-collections-immutable` and the 4 project deps.

3. **`kover` stays per-module.** Applied today in exactly 4 modules
   (`core/domain`, `game/domain`, `ui/presentation`, `addround/presentation`); the root
   build wires it with `kover(projects.*)`, which a precompiled script cannot see.
   It stays `alias(libs.plugins.kover)` in those 4 modules' plugins blocks; it does
   not move into any script.

4. **`compose-runtime` in `core/domain` is not a Compose dependency.** The module
   applies no Compose plugin and does not apply `compose.kts`, yet its commonMain
   declares `libs.compose-runtime`. It is not in the extracted compose set (that set
   is defined by what Compose modules share), so the line **stays** in
   `core/domain`'s commonMain. (If the implementer decides to apply `compose.kts`
   there too, the line is removable — not recommended; the module has no Compose
   sources and pulling in the Compose plugin just for one artifact is scope creep.)

5. **`svg2compose` + KSP wiring stays in `core/presentation`.** The
   `svgToCompose { }` block, the `KspAATask` dependsOn wiring (and its comment), and
   the plugin alias all stay; none of it is parameterizable across modules (only one
   module uses the plugin, and `destinationPackage`/source path are module-specific).

6. **`compose.desktop.currentOs` in a precompiled script.** The type-safe `compose`
   accessor does not exist inside `test.kts`; the extension is reached by
   `extensions.findByType(ComposeExtension::class.java)`. `findByType` (nullable) is
   required because `test.kts` is also applied from the two non-Compose modules
   (`core/domain`, `game/domain`); a `getByType` there would fail. The import is
   `org.jetbrains.compose.ComposeExtension` — the class is on the script's
   compile classpath because `compose-gradle-plugin` is a buildSrc implementation
   dependency (§4).

7. **`players/data` and `game/data` have no tests** — no `test` id in their plugins
   block, and no test deps to remove (they never had any). Their files end as
   plugins + namespace + commonMain.

8. **The root `build.gradle.kts` `allWarningsAsErrors` block is untouched** and
   continues to apply to every KMP module, script-applied or not — no change needed
   there, and it cannot move into the scripts (`projects.*` / cross-project
   configuration).

9. **Config-cache cost.** Per buildsrc.md §5, each `buildSrc` change invalidates the
   configuration cache for the whole build — expected for this one-time migration, but
   the three scripts should land in a single commit so the miss happens once.

## 6. What each module's plugins block becomes

| Module | plugins block after |
|---|---|
| shared | `id("kotlin")`, `id("compose")` |
| core/domain | `id("kotlin")`, `id("test")`, `alias(libs.plugins.kover)` |
| core/presentation | `id("kotlin")`, `id("compose")`, `alias(libs.plugins.svg2compose)` |
| ui/presentation | `id("kotlin")`, `id("compose")`, `id("test")`, `alias(libs.plugins.kover)` |
| app/presentation | `id("kotlin")`, `id("compose")`, `id("test")`, `alias(libs.plugins.kover)` |
| overview/presentation | `id("kotlin")`, `id("compose")` |
| players/domain | `id("kotlin")`, `id("test")` |
| players/data | `id("kotlin")` |
| players/presentation | `id("kotlin")`, `id("compose")` |
| rounds/presentation | `id("kotlin")`, `id("compose")`, `id("test")` |
| addround/presentation | `id("kotlin")`, `id("compose")`, `id("test")`, `alias(libs.plugins.kover)` |
| game/domain | `id("kotlin")`, `id("test")`, `alias(libs.plugins.kover)` |
| game/data | `id("kotlin")` |
| editplayers/presentation | `id("kotlin")`, `id("compose")` |

All module files lose their `ExperimentalWasmDsl` / `JvmTarget` imports (they move
into the scripts), except `core/presentation` which additionally keeps the `KspAATask`
import.
