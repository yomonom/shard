import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
}

group = providers.gradleProperty("plugin.group").get()
version = providers.gradleProperty("plugin.version").get()

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea(providers.gradleProperty("platform.version"))
    }
}

intellijPlatform {
    pluginConfiguration {
        id = providers.gradleProperty("plugin.id")
        name = providers.gradleProperty("plugin.name")
        version = providers.gradleProperty("plugin.version")

        description = providers.fileContents(layout.projectDirectory.file("DESCRIPTION.md")).asText
            .map(::markdownToHTML)

        changeNotes = provider {
            changelog.renderItem(
                (changelog.getOrNull(version.get()) ?: changelog.getUnreleased())
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML
            )
        }

        vendor {
            name = providers.gradleProperty("plugin.vendor.name")
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("plugin.since.build")
            untilBuild = provider { null }
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}

tasks {
    register("getVersion") {
        val version = providers.gradleProperty("plugin.version")

        doLast {
            println(version.get())
        }
    }
}
