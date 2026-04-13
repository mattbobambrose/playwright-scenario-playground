val kotlin_version: String by project
val kotlinx_html_version: String by project
val logback_version: String by project

plugins {
  kotlin("jvm") version "2.3.0"
  id("io.ktor.plugin") version "3.4.2"
}

group = "com.mattbobambrose"
version = "0.0.1"

application {
  mainClass = "io.ktor.server.cio.EngineMain"
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation("io.ktor:ktor-server-call-logging")
  implementation("io.ktor:ktor-server-core")
  implementation("io.ktor:ktor-server-html-builder")
  implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinx_html_version")
  implementation("io.ktor:ktor-server-cio")
  implementation("ch.qos.logback:logback-classic:$logback_version")
  implementation("io.ktor:ktor-server-config-yaml")
  testImplementation("io.ktor:ktor-server-test-host")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
