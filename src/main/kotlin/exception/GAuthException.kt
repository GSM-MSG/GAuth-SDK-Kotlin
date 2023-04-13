package exception

class GAuthException(private val code: Int) : RuntimeException(code.toString()) {
    fun getCode(): Int = code
}