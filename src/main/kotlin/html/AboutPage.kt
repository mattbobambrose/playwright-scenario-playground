package com.mattbobambrose.html

import io.ktor.server.application.ApplicationCall
import kotlinx.html.article
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul

suspend fun ApplicationCall.renderAbout() {
  respondPage("About") {
    article(classes = "prose max-w-3xl") {
      h1(classes = "text-3xl font-bold mb-4") { +"About this demo" }
      p(classes = "mb-4 text-slate-700") {
        +"Bookstore is a deliberately small Ktor application used to demonstrate the "
        +"playwright scenario recording and test-generation skills in this repo. It mixes "
        +"static marketing pages, dynamic HTML DSL views, forms with validation, a session "
        +"cart, hardcoded login, and a drag-and-drop \"My Shelves\" page."
      }
      h2(classes = "text-xl font-semibold mt-6 mb-2") { +"Demo accounts" }
      ul(classes = "list-disc list-inside text-slate-700") {
        li { +"demo / demo" }
        li { +"alice / wonderland" }
        li { +"bob / password1" }
      }
      h2(classes = "text-xl font-semibold mt-6 mb-2") { +"Everything is in memory" }
      p(classes = "text-slate-700") {
        +"Carts, shelves, and orders are reset whenever the server restarts. Treat this "
        +"site like a scratch pad for exercising flows — not a real store."
      }
    }
  }
}
