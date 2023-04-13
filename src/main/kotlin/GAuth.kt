interface GAuth {
    fun generateToken(email: String, password: String, clientId: String, clientSecret: String, redirectUri: String)
}