/**
 * Regex for the dependency-analysis bundle of a KMP artifact family: the group:base
 * coordinate plus every platform variant that BundleHandler.includeDependency does
 * not auto-cover (it only adds -jvm and -android).
 */
fun kmpBundle(base: String): String {
    val (group, name) = base.split(":")
    val escapedGroup = Regex.escape(group)
    val escapedName = Regex.escape(name)
    return "^$escapedGroup:$escapedName(-jvm|-android|-desktop|-graphics|-uikit|-web|-js)?$"
}
