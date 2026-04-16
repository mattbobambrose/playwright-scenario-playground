package com.mattbobambrose.data

import com.mattbobambrose.model.User

object Users {
  val users: Map<String, User> = listOf(
    User("demo", "Demo Reader", "demo"),
    User("alice", "Alice Wonderland", "wonderland"),
    User("bob", "Bob Marlowe", "password1"),
  ).associateBy { it.username }

  fun authenticate(username: String, password: String): User? =
    users[username]?.takeIf { it.password == password }
}
