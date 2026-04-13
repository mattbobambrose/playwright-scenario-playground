package com.mattbobambrose.session

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val username: String)
