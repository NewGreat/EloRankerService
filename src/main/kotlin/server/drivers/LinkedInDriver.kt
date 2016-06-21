package server.drivers

import com.github.scribejava.apis.LinkedInApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb

/**
 * Created by william on 6/20/16.
 */
class LinkedInDriver {
    private val _clientId : String = "77a1nt6yxkjotg"
    private val _clientSecret: String = "EaoQFC1UX742wRBY"
    private val _state: String = "willleestate"
    private val _redirectUri: String = "http://localhost:8000/"
    private val _authorizationUrl: String = "https://www.linkedin.com/oauth/v2/authorization"
    private val _accessUrl: String = "https://www.linkedin.com/oauth/v2/accessToken"
    private val PROTECTED_RESOURCE_URL = "http://api.linkedin.com/v1/people/~/connections:(id,last-name)";

    fun RequestAuthorizationCode(): String {
        val service = ServiceBuilder()
            .apiKey(_clientId)
            .apiSecret(_clientSecret)
            .callback(_redirectUri)
            .build(LinkedInApi.instance())
        val requestToken = service.getRequestToken()
        val oauthUrl = service.getAuthorizationUrl(requestToken)
        println(oauthUrl)
        val oauthVerifier = "76672"
        val accessToken = service.getAccessToken(requestToken, oauthVerifier)
        val request = OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service)
        service.signRequest(accessToken, request)
        val response = request.send()
        println(response.body)

//        val (request, response, result) = "$_authorizationUrl?response_type=code&client_id=$_clientId&redirect_uri=$_redirectUri&state=$_state&scope=r_basicprofile&format=json"
//            .httpGet().responseString()
//        println("************* RESPONSE *************")
//        println(response)
//        println("************* RESULT *************")
//        println(result)

        return response.toString()
    }
}