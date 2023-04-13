package response

data class GAuthUserInfo(
    val email: String,
    val name: String,
    val grade: Int?,
    val classNum: Int?,
    val num: Int?,
    val gender: String?,
    val profileUrl: String?,
    val role: String?
) {
    constructor(map: Map<String, Any>) : this(
        email = map["email"] as String,
        name = map["name"] as String,
        grade = map["grade"] as? Int,
        classNum = map["classNum"] as? Int,
        num = map["num"] as? Int,
        gender = map["gender"] as? String,
        profileUrl = map["profileUrl"] as? String,
        role = map["role"] as? String
    )
}