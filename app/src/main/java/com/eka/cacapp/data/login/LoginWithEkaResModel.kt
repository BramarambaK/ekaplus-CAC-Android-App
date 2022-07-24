package com.eka.cacapp.data.login

/**
 * Data class for Login With Eka response
 * */

data class LoginWithEkaResModel(
        val tokenResponse: TokenResponse,
        val userInfo: UserInfo
)

data class TokenResponse(
        val auth2AccessToken: Auth2AccessToken,
        val sessionTimeoutInSeconds: Int
)

data class UserInfo(
    val activityType: Any,
    val clientId: Int,
    val clientName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val pwd: Any,
    val tenantShortName: String,
    val userId: Int,
    val userName: String,
    val userType: Int
)

data class Auth2AccessToken(
    val access_token: String,
    val expires_in: Int,
    val jti: String,
    val refresh_token: String,
    val scope: String,
    val token_type: String
)