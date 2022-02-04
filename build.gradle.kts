import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val jsoupVersion: String by project

plugins {
    kotlin("jvm") version "1.6.10"

}

group = "me.xiaocao"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}


dependencies {
    api("org.jsoup", "jsoup", jsoupVersion)
    api("io.ktor", "ktor-client-core", ktorVersion)
    api("io.ktor", "ktor-client-core", ktorVersion)
    api("io.ktor", "ktor-client-okhttp", ktorVersion)
    testImplementation(kotlin("test", "1.6.10"))
}