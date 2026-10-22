import de.fayard.refreshVersions.core.StabilityLevel

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    // See https://splitties.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.6"
}

refreshVersions {
    // Material icons have a separate release cycle from Material components.
    extraArtifactVersionKeyRules("""
        androidx.compose.material:material-icons-*
        ^^^^^^^^.^^^^^^^.         ^^^^^^^^^^^^^^
    """.trimIndent())
    rejectVersionIf {
        candidate.stabilityLevel != StabilityLevel.Stable
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "NYC Airport Security Line Waits"
include(":app")
