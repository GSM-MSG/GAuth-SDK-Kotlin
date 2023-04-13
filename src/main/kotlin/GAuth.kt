interface GAuth {
    fun generateToken(email: String, password: String, clientId: String, clientSecret: String, redirectUri: String): GAuthToken
    fun generateToken(code: String, clientId: String, clientSecret: String, redirectUri: String): GAuthToken
    fun generateCode(email: String, password: String): GAuthCode
    fun refresh(refreshToken: String): GAuthToken
    fun getUserInfo(accessToken: String): GAuthUserInfo
}