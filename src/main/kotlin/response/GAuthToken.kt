package response

data class GAuthToken(
    val accessToken: String,
    val refreshToken: String
) {
    constructor(map: Map<String, String>) : this(
        accessToken = map["accessToken"] as String,
        refreshToken = map["refreshToken"] as String
    )
}
