package com.mattbobambrose.html

import com.mattbobambrose.data.Stores
import com.mattbobambrose.session.UserSession
import com.mattbobambrose.session.cartKeyOrNull
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtml
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.HtmlBlockTag
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.form
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.nav
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.title

suspend fun ApplicationCall.respondPage(
  pageTitle: String,
  status: HttpStatusCode = HttpStatusCode.OK,
  content: BODY.() -> Unit,
) {
  val session = sessions.get<UserSession>()
  val cartCount = Stores.cartCountFor(cartKeyOrNull())
  respondHtml(status) {
    head {
      meta(charset = "utf-8")
      meta(name = "viewport", content = "width=device-width, initial-scale=1")
      title { +"$pageTitle · Bookstore Demo" }
      script(src = "https://cdn.tailwindcss.com") {}
      link(rel = "icon", href = "data:,")
    }
    body(classes = "min-h-screen bg-slate-50 text-slate-900 flex flex-col") {
      renderHeader(session, cartCount)
      main(classes = "flex-1 max-w-6xl w-full mx-auto px-4 py-8") {
        content(this@body)
      }
      renderFooter()
    }
  }
}

private fun BODY.renderHeader(session: UserSession?, cartCount: Int) {
  header(classes = "bg-white border-b border-slate-200 shadow-sm") {
    div(classes = "max-w-6xl mx-auto px-4 py-4 flex items-center gap-6") {
      a(href = "/", classes = "text-xl font-bold text-indigo-700") {
        attributes["data-testid"] = "brand"
        +"📚 Bookstore"
      }
      nav(classes = "flex items-center gap-4 text-sm font-medium text-slate-700 flex-1") {
        navLink("/catalog", "Catalog", "nav-catalog")
        if (session != null) {
          navLink("/shelves", "My Shelves", "nav-shelves")
          navLink("/orders", "Orders", "nav-orders")
        }
        navLink("/about", "About", "nav-about")
      }
      a(href = "/cart", classes = "relative inline-flex items-center gap-1 text-sm font-semibold text-slate-700 hover:text-indigo-700") {
        attributes["data-testid"] = "nav-cart"
        +"Cart"
        span(classes = "ml-1 inline-flex items-center justify-center min-w-[1.5rem] h-6 px-2 rounded-full bg-indigo-600 text-white text-xs") {
          attributes["data-testid"] = "cart-count"
          +cartCount.toString()
        }
      }
      if (session == null) {
        a(href = "/login", classes = "text-sm font-semibold text-indigo-700 hover:underline") {
          attributes["data-testid"] = "nav-login"
          +"Log in"
        }
      } else {
        div(classes = "flex items-center gap-3") {
          span(classes = "text-sm text-slate-600") {
            attributes["data-testid"] = "nav-user"
            +"Hi, ${session.username}"
          }
          form(action = "/logout", method = FormMethod.post) {
            button(type = ButtonType.submit, classes = "text-sm font-semibold text-slate-600 hover:text-red-600") {
              attributes["data-testid"] = "nav-logout"
              +"Log out"
            }
          }
        }
      }
    }
  }
}

private fun FlowContent.navLink(href: String, label: String, testId: String) {
  a(href = href, classes = "hover:text-indigo-700") {
    attributes["data-testid"] = testId
    +label
  }
}

private fun BODY.renderFooter() {
  footer(classes = "bg-white border-t border-slate-200 mt-12") {
    div(classes = "max-w-6xl mx-auto px-4 py-6 text-sm text-slate-500 flex justify-between") {
      span { +"© Bookstore Demo" }
      a(href = "/about", classes = "hover:text-indigo-700") { +"About this demo" }
    }
  }
}

fun HtmlBlockTag.flashError(message: String?) {
  if (message != null) {
    div(classes = "mb-4 rounded border border-red-300 bg-red-50 px-4 py-3 text-red-800") {
      attributes["data-testid"] = "flash-error"
      +message
    }
  }
}

fun HtmlBlockTag.fieldError(error: String?) {
  if (error != null) {
    div(classes = "mt-1 text-sm text-red-600") {
      attributes["data-field-error"] = "true"
      +error
    }
  }
}

fun HtmlBlockTag.textField(
  fieldName: String,
  labelText: String,
  value: String,
  error: String? = null,
  type: InputType = InputType.text,
) {
  div {
    label(classes = "block text-sm font-medium text-slate-700") {
      attributes["htmlFor"] = fieldName
      +labelText
    }
    input(type = type, name = fieldName, classes = "mt-1 w-full rounded border border-slate-300 px-3 py-2") {
      attributes["id"] = fieldName
      attributes["data-testid"] = "input-$fieldName"
      this.value = value
    }
    fieldError(error)
  }
}

