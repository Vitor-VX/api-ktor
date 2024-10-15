plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "victor.server.kt"
version = "1.0.0"
application {
    mainClass.set("victor.server.kt.ApplicationKt")
    //${extra["io.ktor.development"] ?: "false"}
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

dependencies {
    implementation("io.ktor:ktor-server-status-pages:2.0.20")

    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}