package com.mattbobambrose.html

import io.ktor.server.application.ApplicationCall
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.hiddenInput
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.section
import kotlinx.html.ul

suspend fun ApplicationCall.renderLogin(
  next: String,
  username: String = "",
  error: String? = null,
) {
  respondPage("Log in") {
    section(classes = "max-w-md mx-auto bg-white rounded-xl border border-slate-200 p-8") {
      attributes["data-testid"] = "login-section"
      h1(classes = "text-2xl font-bold mb-4") { +"Log in" }
      flashError(error)
      form(action = "/login", method = FormMethod.post, classes = "space-y-4") {
        attributes["data-testid"] = "login-form"
        hiddenInput(name = "next") { value = next }
        textField("username", "Username", username)
        textField("password", "Password", "", type = InputType.password)
        button(type = ButtonType.submit, classes = "w-full rounded bg-indigo-600 text-white font-semibold px-4 py-2 hover:bg-indigo-700") {
          attributes["data-testid"] = "login-submit"
          +"Log in"
        }
      }
      div(classes = "mt-6 text-sm text-slate-500") {
        p(classes = "font-semibold text-slate-700") { +"Demo accounts:" }
        ul(classes = "list-disc list-inside mt-1") {
          li { +"demo / demo" }
          li { +"alice / wonderland" }
          li { +"bob / password1" }
        }
      }
    }
  }
}
