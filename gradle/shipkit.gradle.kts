apply(plugin="org.shipkit.shipkit-auto-version")
apply(plugin="org.shipkit.shipkit-changelog")
apply(plugin="org.shipkit.shipkit-github-release")

tasks.named("generateChangelog") {
    val previousRevision = project.extra["shipkit-auto-version.previous-tag"]
    val githubToken = System.getenv("GITHUB_TOKEN")
    val repository = "nettyplus/netty-leak-detector-junit-extension"
    // Workarounds for https://github.com/shipkit/shipkit-changelog/issues/103
    doNotTrackState("GenerateChangelogTask tracks the entire repo, which results is locking problems hashing the .gradle folder.")
    // GenerateChangelogTask uses the entire repo as input, which means it needs to "depend on" all other tasks" outputs.
    // mustRunAfter(allprojects.collectMany { it.tasks }.grep { it.path != ":generateChangelog" && it.path != ":githubRelease" })
}

tasks.named("githubRelease") {
    val genTask = tasks.named("generateChangelog").get()
    dependsOn(genTask)
    // val repository = genTask.repository
    // val changelog = genTask.outputFile
    val newTagRevision = System.getenv("GITHUB_SHA")
    val githubToken = System.getenv("GITHUB_TOKEN")
}

val version = rootProject.version as String
val isSnapshot = version.endsWith("-SNAPSHOT")
if (isSnapshot) {
    tasks.named("githubRelease") {
        //snapshot versions do not produce changelog / GitHub releases
        enabled = false
    }
}

tasks.register("releaseSummary") {
    doLast {
        if (isSnapshot) {
            println("RELEASE SUMMARY\n" +
                "  SNAPSHOTS released to: https://s01.oss.sonatype.org/content/repositories/snapshots/io.github.nettyplus/netty-leak-detector-junit-extension\n" +
                "  Release to Maven Central: SKIPPED FOR SNAPSHOTS\n" +
                "  Github releases: SKIPPED FOR SNAPSHOTS")
        } else {
            println("RELEASE SUMMARY\n" +
                "  Release to Maven Central (available in few hours): https://repo1.maven.org/maven2/io.github.nettyplus/netty-leak-detector-junit-extension/\n" +
                "  Github releases: https://github.com/nettyplus/netty-leak-detector-junit-extension/releases")
        }
    }
}
