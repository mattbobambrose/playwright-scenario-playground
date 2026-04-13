plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ktor)
  alias(libs.plugins.versions)
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
  implementation(libs.ktor.server.call.logging)
  implementation(libs.ktor.server.core)
  implementation(libs.ktor.server.html.builder)
  implementation(libs.kotlinx.html)
  implementation(libs.ktor.server.cio)
  implementation(libs.logback.classic)
  implementation(libs.ktor.server.config.yaml)
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.ktor.server.sessions)
  implementation(libs.ktor.server.status.pages)
  implementation(libs.ktor.serialization.kotlinx.json)
  testImplementation(libs.ktor.server.test.host)
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
}

tasks.test {
  useJUnitPlatform()
}
