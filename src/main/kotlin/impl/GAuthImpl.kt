package impl

import GAuth
import GAuthCode
import GAuthToken
import GAuthUserInfo
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import enums.TokenType
import exception.GAuthException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.json.simple.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class GAuthImpl(
    private val mapper: ObjectMapper,
): GAuth {

    companion object {
        const val GAUTH_SERVER_URL = "https://server.gauth.co.kr/oauth";
        const val RESOURCE_SERVER_URL = "https://open.gauth.co.kr"
    }

    override fun generateToken(
        email: String,
        password: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
    ): GAuthToken {
        TODO("Not yet implemented")
    }

    override fun generateToken(code: String, clientId: String, clientSecret: String, redirectUri: String): GAuthToken {
        TODO("Not yet implemented")
    }

    override fun generateCode(email: String, password: String): GAuthCode {
        TODO("Not yet implemented")
    }

    override fun refresh(refreshToken: String): GAuthToken {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(accessToken: String): GAuthUserInfo {
        TODO("Not yet implemented")
    }

    private fun getToken(code: String, clientId: String, clientSecret: String, redirectUri: String): Map<String, String> {
        val body = hashMapOf(
            "code" to code,
            "clientId" to clientId,
            "clientSecret" to clientSecret,
            "redirectUri" to redirectUri
        )
        return sendPostGAuthServer(body, null, "/token")
    }

    private fun sendPostGAuthServer(body: Map<String, String>, token: String?, url: String): Map<String, String> =
        sendPost(body, token, GAUTH_SERVER_URL + url)


    private fun sendPost(body: Map<String, String>, token: String?, url: String): Map<String, String> {
        val request = HttpPost(url)

        request.apply {
            setHeader("Accept", "application/json")
            setHeader("Connection", "keep-alive")
            setHeader("Content-Type", "application/json")
            addHeader("Authorization", token)
        }

        if(body.isNotEmpty()){
            val json = JSONObject(body).toJSONString()
            request.entity = StringEntity(json)
        }

        val client = HttpClientBuilder.create().build()
        val response = client.execute(request)
        val responseStatus = response.statusLine.statusCode

        if(responseStatus != 200)
            throw GAuthException(responseStatus)

        val bufferedReader = BufferedReader(InputStreamReader(response.entity.content, StandardCharsets.UTF_8))
        val responseBody = bufferedReader.readLine()
        bufferedReader.close()

        val typeReference: TypeReference<Map<String, String>> = object : TypeReference<Map<String, String>>() {}

        return mapper.readValue(responseBody, typeReference)
    }


}