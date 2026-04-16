package com.mattbobambrose

import com.mattbobambrose.data.Stores
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication

fun ApplicationTestBuilder.testClient(): HttpClient = createClient {
  install(HttpCookies)
}

fun runTestApp(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
  Stores.resetAll()
  testApplication {
    application { module() }
    val client = testClient()
    block(client)
  }
}

suspend fun HttpClient.followRedirectAsGet(response: HttpResponse): HttpResponse {
  if (response.status.isSuccess()) return response
  val location = response.headers[HttpHeaders.Location] ?: return response
  return get(location)
}

suspend fun HttpClient.postFormFollow(url: String, vararg form: Pair<String, String>): HttpResponse {
  val res = submitForm(
    url = url,
    formParameters = parameters { form.forEach { (k, v) -> append(k, v) } },
  )
  return followRedirectAsGet(res)
}

suspend fun HttpClient.loginAs(username: String, password: String) {
  submitForm(
    url = "/login",
    formParameters = parameters {
      append("username", username)
      append("password", password)
      append("next", "/")
    },
  )
}
