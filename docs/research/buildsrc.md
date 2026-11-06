# buildSrc and precompiled script plugins

Research note: how Gradle's `buildSrc` works, what precompiled script plugins can and
cannot do, and how both interact with the version catalog and typesafe project
accessors. Grounded in this repo: Kotlin Multiplatform, Kotlin 2.4.21, Gradle 9.8.2
(`gradle/wrapper/gradle-wrapper.properties`), a single version catalog at
`gradle/libs.versions.toml`, `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` in
`settings.gradle.kts`, and configuration cache on (`gradle.properties`).

## 1. How buildSrc works

`buildSrc` is a special directory Gradle recognizes at the root of a build. When present,
Gradle treats it as an independent, automatically included build: it has its own
`build.gradle.kts`, its own `src/` layout, and direct access to the Gradle API
(Gradle docs, "Sharing Build Logic using buildSrc").

Concretely, upon detecting the directory Gradle:

- compiles all classes and scripts in `buildSrc` **before evaluating any other build
  script** in the main build;
- puts the compiled output on the **classpath of all build scripts** — root and every
  subproject — so plugins and helpers from `buildSrc` are usable in any build script with
  no extra setup;
- in a multi-project build allows **exactly one** `buildSrc`, and it must sit in the root
  project directory.

For Gradle to compile `.gradle.kts` files in `buildSrc` into plugins, the `buildSrc`
project must apply the `kotlin-dsl` plugin in its own `build.gradle.kts` (Gradle docs,
"Organizing build logic"). The `kotlin-dsl` plugin applies the Kotlin JVM plugin, adds
`kotlin-stdlib`/`kotlin-reflect` and `gradleKotlinDsl()` to the compile classpath, and
enables precompiled script plugin support; its version is pinned per Gradle release and
must not be specified in `plugins {}` (Gradle docs, "Gradle Kotlin DSL Primer").

The cost: changes to `buildSrc` **invalidate the configuration phase** of the main build.
The Gradle docs state this directly — "Changes to code in `buildSrc` will invalidate the
configuration phase and require re-execution of all tasks" — and the configuration cache
documentation lists "Contents of `buildSrc`" among the inputs that determine a cache hit,
so any `buildSrc` change is a cache miss for the whole build (Gradle docs, "Configuration
Cache").

## 2. Precompiled script plugins

A precompiled script plugin is a `.gradle.kts` (Kotlin) or `.gradle` (Groovy) script stored
in a plugin source set — in this case `buildSrc/src/main/kotlin`. Gradle compiles it into
a real plugin class, packages it like a normal plugin, and derives the plugin ID from the
file name (Gradle docs, "Pre-compiled Script Plugins", Gradle 9.8.2).

### Plugin ID

- The ID is the file name without the `.gradle.kts` suffix. **Dashes are kept, not
  converted to dots**: `code-quality.gradle.kts` → plugin ID `code-quality`, applied as
  `id("code-quality")`. (The dash→dot nesting rule belongs to version-catalog accessors,
  §3, not plugin IDs.)
- A **package declaration prefixes the ID**: `code-quality.gradle.kts` in
  `src/main/kotlin/my` with `package my` → `my.code-quality`. Groovy precompiled scripts
  cannot have packages (same doc).
- The suffix fixes the plugin scope: `.gradle.kts` → `Plugin<Project>`,
  `.settings.gradle.kts` → `Plugin<Settings>`, `.init.gradle.kts` → `Plugin<Gradle>`.
- IDs must not start with `org.gradle` and must not clash with a core plugin ID
  (Gradle docs, "Introduction to Plugins").

### What a precompiled script can and cannot do

A precompiled script is a full build script body: it may apply plugins, configure
extensions, register tasks, and declare dependencies. What is forbidden is pinning
versions inside it:

- `version "1.2.3"` and `apply false` **are not supported** in the `plugins {}` block of a
  precompiled script. The plugin version is always the one declared in the plugin's
  (`buildSrc/`) build file (Gradle docs, "Pre-compiled Script Plugins" → "Applying
  external plugins").
- To apply an external (non-core) plugin, it must be on the plugin project's
  `implementation` classpath — i.e. an `implementation(...)` dependency in
  `buildSrc/build.gradle.kts` — and then referenced by plain `id("…")` in the script. If
  a plugin only needs to be on the classpath and not applied, it is omitted from the
  `plugins {}` block and added as a dependency only.

So for this repo, a precompiled script that configures `org.jetbrains.kotlin.multiplatform`
or `com.android.kotlin.multiplatform.library` requires those plugins on `buildSrc`'s
implementation classpath (see §4), and the script's `plugins {}` block carries bare IDs.

### The version-catalog sharp edge

Type-safe `libs` accessors inside a precompiled script **do not resolve**. The accessors
are generated per build, for that build's own scripts; a precompiled script compiles in the
`buildSrc` build and is applied in the consuming project, so neither accessor is in scope
the way it is in a normal build script:

- `buildSrc` does **not** inherit the main build's version catalog (Gradle docs, "Version
  Catalogs" → "Using a catalog in `buildSrc`").
- The consuming project's `libs` extension is generated for the consuming build's own
  scripts, not for build logic compiled in `buildSrc`.
- The `plugins {}` block of a precompiled script **cannot use the catalog at all**
  (`alias(libs.plugins.…)` is unavailable there) (same doc).

The supported access is through the catalog extension, resolved at configuration time.
Since Gradle 8.5, a `versionCatalogs` extension accessor is available directly in Kotlin
precompiled scripts (Gradle 8.5 release notes, "Version catalog API in precompiled
scripts"); equivalently, via the version catalog API:

```kotlin
// buildSrc/src/main/kotlin/kotlin.kts
val libs = versionCatalogs.named("libs")          // throws if the catalog is absent
dependencies {
    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
}
```

Note this resolves the **consuming project's** catalog — the same file the main build uses
— because the accessor reads the project the script is applied to, not the `buildSrc`
build. That is precisely why a `libs` catalog does not even need to be declared in
`buildSrc/settings.gradle.kts` for this purpose: declaring one there only makes `libs`
usable in `buildSrc/build.gradle.kts` itself, and then two copies of the catalog file
must be kept in sync.

## 3. Version catalog and typesafe project accessors in consuming projects

Both of these are per-build generated accessors, and that is exactly why precompiled
scripts interact with them awkwardly.

**Version catalog (`libs`).** Accessors are generated for each catalog per build and
exposed on the projects of that build (Gradle docs, "Version Catalogs"). A dash or
underscore in an alias maps to a dot in the accessor: `ktor-client-core` →
`libs.ktor.client.core` (same doc, "Aliases and type-safe accessors"). In this repo the
single `gradle/libs.versions.toml` feeds the `libs` accessor in every module's build
script. Precompiled scripts instead use the `versionCatalogs` extension or
`extensions.getByType(VersionCatalogsExtension::class.java).named("libs")` (§2). A
consequence: a precompiled script cannot write `libs.versions.kotlin.get()` to read a
version — it must go through the catalog API.

**Typesafe project accessors (`projects.*`).** Enabled here via
`enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` in `settings.gradle.kts`. Project
path `:a:b` maps to `projects.a.b`, kebab/snake segments to camelCase (`:some-lib` →
`projects.someLib`); the accessors are generated as an extension on the root project
(Gradle docs, "Declaring dependencies" → "Type-safe project dependencies"). They do **not
resolve inside `buildSrc` precompiled scripts**: `buildSrc` is a separate build that
knows nothing about the main build's project structure, so `projects.shared.features.…`
is not available in a convention script. This matters for the current root
`build.gradle.kts`, which wires Kover per-module with
`kover(projects.shared.features.core.domain)` etc. — that wiring must stay in the root
build script (or be driven by a loop over `subprojects { }`), not move into a
precompiled script.

## 4. buildSrc as a standalone Gradle project

Because `buildSrc` is its own build, it has its own `build.gradle.kts` and its own
dependency declarations, and those dependencies are what put plugin jars on the
implementation classpath every precompiled script can apply:

```kotlin
// buildSrc/build.gradle.kts
plugins {
    `kotlin-dsl`   // version is pinned to the running Gradle release; never specified
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    // Plugin jars: group:artifact:version, versions pinned to gradle/libs.versions.toml.
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.21")
    implementation("com.android.tools.build:gradle:9.3.3")
}
```

Two points to keep it honest:

- **Version pinning must stay consistent by hand.** The repo pins every version in one
  `gradle/libs.versions.toml` (Kotlin 2.4.21, AGP 9.3.3, Compose Multiplatform 1.12.2).
  The catalog cannot be used inside `buildSrc/build.gradle.kts`'s `plugins {}` block, and
  even for its `implementation(...)` lines the cleanest single-source approach is to
  import the same file into the `buildSrc` build:

  ```kotlin
  // buildSrc/settings.gradle.kts
  dependencyResolutionManagement {
      versionCatalogs {
          create("libs") {
              from(files("../gradle/libs.versions.toml"))
          }
      }
  }
  ```

  with `buildSrc/build.gradle.kts` then using `libs` there (Gradle docs, "Version
  Catalogs" → "Using a catalog in `buildSrc`"). Without that import, every version
  referenced in `buildSrc` is a literal that must be kept equal to the toml manually.
- **Plugin coordinates ≠ plugin IDs.** `implementation("com.android.tools.build:gradle:9.3.3")`
  is the artifact that backs plugin ID `com.android.application`; the script applies the
  ID, the dependency supplies the version. For plugins whose artifact is not the plugin
  marker, use the plugin's jar coordinate, or map a catalog plugin entry to
  `"<pluginId>:<pluginId>.gradle.plugin:<version>"` (Gradle docs, "Version Catalogs" →
  "Using a catalog in `buildSrc`" shows this helper).

## 5. Limitations and gotchas

- **Recompile cost + config-cache invalidation.** Any change under `buildSrc` recompiles
  the `buildSrc` build and, because buildSrc output is a configuration input, invalidates
  the configuration cache for the whole main build — every task re-runs the configuration
  phase (Gradle docs, "Configuration Cache"; "Sharing Build Logic using buildSrc"). This
  is the main reason the config cache is off-limits for a fast local loop while editing
  build logic, and the standing reason to keep `buildSrc` changes rare and review-worthy.
- **`buildSrc` vs `build-logic` (included build).** An included build named e.g.
  `build-logic` gets its own up-to-date checks and can be worked on and tested in the IDE
  as a normal project; `buildSrc` is always rebuilt before the main build and is the less
  flexible of the two (Gradle docs, "Structuring Multi-Project Builds"; "Sharing Build
  Logic using buildSrc").
- **Testing.** `buildSrc` is a standalone build, so it *can* carry a test source set, but
  there is no first-class way to unit-test a precompiled script plugin as a unit: the
  script is a plugin, and the practical options are Gradle TestKit (`GradleRunner`)
  functional tests or extracting the logic into plain classes and testing those (Gradle
  docs, "Sharing Build Logic using buildSrc" — "Ease of Testing"; "Pre-compiled Script
  Plugins" — "Faster adoption").
- **No `plugins {}` catalog, no `version`/`apply false`** in precompiled scripts (§2).
- **Catalog accessors are per build** (§2, §3): no `libs` in precompiled scripts, no
  `projects.*` either; use the catalog extension / `VersionCatalogsExtension` API.
- **IDE support** is the practical win of precompiled scripts — they are real Kotlin files
  in a real build, so IntelliJ navigation and autocomplete work (Gradle docs,
  "Pre-compiled Script Plugins" — benefits list).

## 6. Decision: precompiled scripts over convention (class) plugins

This repo's repeated build logic lives in two places today:

- the KMP target block — `kotlin { android { namespace = …; compileSdk = …; minSdk = …;
  compilerOptions { jvmTarget = JvmTarget.JVM_11 } }, jvm(), wasmJs { nodejs() … } }` —
  duplicated across all 17 module build files under `shared/` and the app modules;
- the root `build.gradle.kts` `subprojects { plugins.withId(…) { … allWarningsAsErrors … } }`
  cross-project configuration, which the Gradle docs explicitly advise against in favour
  of convention plugins (Gradle docs, "Convention Plugins" — "Convention plugins are
  preferred over `allprojects {}` / `subprojects {}` blocks").

Plan: extract both into precompiled script plugins in `buildSrc/src/main/kotlin` —
`kotlin.kts` (common Kotlin/KMP target + compiler options), `compose.kts` (Compose
Multiplatform + compiler wiring), `test.kts` (test-framework conventions) — and apply them
by ID from each module's `plugins {}` block, replacing the `subprojects {}` blocks and the
copy-pasted target config.

Rationale in one line: precompiled scripts give the same extraction as convention
(class) plugins with far less machinery — no `Plugin<*>` classes, no
`gradlePlugin { }` registration, no publishing setup — while staying real Kotlin files the
IDE navigates, and the version-catalog/`projects` limitations above are worked around
with the `versionCatalogs` extension and keeping the `projects.*`-dependent Kover wiring
in the root build script.
