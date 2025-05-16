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

rootProject.name = "ru.mirea.Burmistrov.Lesson3"
include(":app")
include(":intentapp")
include(":Sharer")
include(":favoritebook")
include(":systemintentsapp")
include(":simplefragmentapp")
include(":faildproject")
include(":mireaproject")
