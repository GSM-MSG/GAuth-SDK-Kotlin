interface GAuth {
    fun generateToken(email: String, password: String, clientId: String, clientSecret: String, redirectUri: String)
    fun generateToken(code: String, clientId: String, clientSecret: String, redirectUri: String)
    fun generateCode(email: String, password: String)
    fun getUserInfo(accessToken: String)
}