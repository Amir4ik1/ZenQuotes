pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ZenQuotes"
include(":app")
include(":core")
include(":data")
include(":domain")
include(":common")
include(":features:feature_favorites")
include(":features:feature_quotes")
include(":features:feature_share")
include(":features:feature_intro")
include(":features:feature_widget")
include(":themes")
