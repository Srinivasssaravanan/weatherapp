// Project-level build.gradle

plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        // jcenter is deprecated, consider removing if possible
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
