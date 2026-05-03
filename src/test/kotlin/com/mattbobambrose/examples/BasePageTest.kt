package com.mattbobambrose.examples

import com.mattbobambrose.data.Stores
import com.mattbobambrose.module
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import java.net.ServerSocket

abstract class BasePageTest(private val path: String) : StringSpec() {
  private lateinit var playwright: Playwright
  private lateinit var browser: Browser
  private var server: EmbeddedServer<*, *>? = null
  private var baseUrl: String = ""
  private lateinit var context: BrowserContext
  protected lateinit var page: Page

  override suspend fun beforeSpec(spec: Spec) {
    val port = ServerSocket(0).use { it.localPort }
    baseUrl = "http://127.0.0.1:$port"
    server = embeddedServer(CIO, port = port, host = "127.0.0.1") { module() }
      .also { it.start(wait = false) }
    playwright = Playwright.create()
    browser = playwright.chromium().launch()
  }

  override suspend fun beforeTest(testCase: TestCase) {
    Stores.resetAll()
    context = browser.newContext(
      Browser.NewContextOptions()
        .setViewportSize(1440, 900)
        .setBaseURL(baseUrl),
    )
    page = context.newPage()
    page.navigate(path)
    page.waitForLoadState()
  }

  override suspend fun afterTest(testCase: TestCase, result: TestResult) {
    context.close()
  }

  override suspend fun afterSpec(spec: Spec) {
    browser.close()
    playwright.close()
    server?.stop(500, 1000)
  }
}
