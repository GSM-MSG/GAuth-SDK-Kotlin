package impl

import GAuth
import GAuthCode
import GAuthToken
import GAuthUserInfo
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import enums.TokenType
import exception.GAuthException
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
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
        val code = generateCode(email, password).code
        return GAuthToken(getToken(code, clientId, clientSecret, redirectUri))
    }

    override fun generateToken(code: String,
                               clientId: String,
                               clientSecret: String,
                               redirectUri: String
    ): GAuthToken =
        GAuthToken(getToken(code, clientId, clientSecret, redirectUri))

    override fun generateCode(email: String, password: String): GAuthCode {
        val body = hashMapOf<String, String>(
            "email" to email,
            "password" to password
        )
        val code = sendPostGAuthServer(body, null, "/code").get("code")
        return GAuthCode(code!!)
    }

    override fun refresh(refreshToken: String): GAuthToken {
        val refreshHeader = if (refreshToken.startsWith("Bearer ")) refreshToken else "Bearer $refreshToken"
        return GAuthToken(sendPatchGAuthServer(null, refreshHeader, "/user", TokenType.REFRESH))
    }

    override fun getUserInfo(accessToken: String): GAuthUserInfo {
        val authHeader = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        return GAuthUserInfo(sendGetResourceServer(authHeader, "/user"))
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

    private fun sendGetResourceServer(token: String?, url: String) =
        sendGet(token, RESOURCE_SERVER_URL + url)

    private fun sendPostGAuthServer(body: Map<String, String>, token: String?, url: String): Map<String, String> =
        sendPost(body, token, GAUTH_SERVER_URL + url)

    private fun sendPatchGAuthServer(body: Map<String, String>?, token: String?, url: String, tokenType: TokenType) =
        sendPatch(body , token, GAUTH_SERVER_URL + url , tokenType)

    private fun sendGet(token: String?, url: String): Map<String, Any> {
        val request = HttpGet(url)
        request.addHeader("Authorization", token)

        val client = getClient(request)
        val response = getResponse(client, request)
        val responseStatus = getResponseStatus(response)

        if(responseStatus != 200)
            throw GAuthException(responseStatus)
        val bufferedReader = BufferedReader(InputStreamReader(response.entity.content, StandardCharsets.UTF_8))
        val responseBody = bufferedReader.readLine()
        val typeReference: TypeReference<Map<String, Any>> = object : TypeReference<Map<String, Any>>() {}
        bufferedReader.close()
        return mapper.readValue(responseBody, typeReference)
    }

    private fun sendPost(body: Map<String, String>, token: String?, url: String): Map<String, String> {
        val request = HttpPost(url)

        request.apply {
            setHeader("Accept", "application/json")
            setHeader("Connection", "keep-alive")
            setHeader("Content-Type", "application/json")
            addHeader("Authorization", token)
        }

        body?.let {
            val json = JSONObject(body).toJSONString()
            request.entity = StringEntity(json)
        }

        val client = getClient(request)
        val response = getResponse(client, request)
        val responseStatus = getResponseStatus(response)

        if(responseStatus != 200)
            throw GAuthException(responseStatus)

        val bufferedReader = BufferedReader(InputStreamReader(response.entity.content, StandardCharsets.UTF_8))
        val responseBody = bufferedReader.readLine()
        bufferedReader.close()

        val typeReference: TypeReference<Map<String, String>> = object : TypeReference<Map<String, String>>() {}

        return mapper.readValue(responseBody, typeReference)
    }

    private fun sendPatch(body: Map<String, String>?, token: String?, url: String, tokenType: TokenType): Map<String, String> {
        val request = HttpPatch(url)
        request.apply {
            setHeader("Accept", "application/json");
            setHeader("Connection", "keep-alive");
            setHeader("Content-Type", "application/json");
        }

        if(tokenType == TokenType.ACCESS)
            request.setHeader("Authorization", token)
        else
            request.addHeader("refreshToken", token)

        body?.let {
            val json = JSONObject(it).toString()
            request.entity = StringEntity(json)
        }

        val client = getClient(request)
        val response = getResponse(client, request)
        val responseStatus = getResponseStatus(response)

        if(responseStatus != 200)
            throw GAuthException(responseStatus)

        val bufferedReader = BufferedReader(InputStreamReader(response.entity.content, StandardCharsets.UTF_8))
        val responseBody = bufferedReader.readLine()
        bufferedReader.close()

        val typeReference: TypeReference<Map<String, String>> = object : TypeReference<Map<String, String>>() {}

        return mapper.readValue(responseBody, typeReference)
    }

    private fun getClient(request: HttpRequestBase) = HttpClientBuilder.create().build()

    private fun getResponse(client: CloseableHttpClient, request: HttpRequestBase) = client.execute(request)

    private fun getResponseStatus(response: CloseableHttpResponse) = response.statusLine.statusCode
}