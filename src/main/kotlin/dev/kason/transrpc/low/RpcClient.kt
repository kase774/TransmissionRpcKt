package dev.kason.transrpc.low

import dev.kason.transrpc.isObject
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val defaultTransmissionUrl = Url("http://localhost:9091/transmission/rpc")
const val CSRF_HEADER = "X-Transmission-Session-Id"
const val COMPATIBLE_VERSION = "4.0.6"

fun createSimpleHttpClient(timeout: Duration = 5.seconds): HttpClient = HttpClient {
    install(HttpTimeout) {
        requestTimeoutMillis = timeout.inWholeMilliseconds
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.DEFAULT
        sanitizeHeader {
            it == HttpHeaders.Authorization
        }
    }
}

private val rpcClientLogger by lazy { KotlinLogging.logger { } }
private val defaultJson = Json { encodeDefaults = true }

/** Represents a rpc request the client can send, where [T] is the type of the response that
 * we expect from the server in response. When serialized, should match the "arguments" property
 * of the request, as per chapter 2.1 */
@Serializable(with = RpcRequest.Serializer::class)
sealed class RpcRequest<T : RpcResponse> {
    abstract val method: String

    object Serializer : JsonContentPolymorphicSerializer<RpcRequest<*>>(RpcRequest::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<RpcRequest<*>> =
            error("should not be deserializing response!!")
    }
}

/** The response that we expect from the server for a given [RpcRequest]. This is specifically
 * the arguments property; if the operation is not successful it is logged. */
@Serializable
sealed interface RpcResponse

/** Represents the RpcResponse for all the requests that do not expect a response */
@Serializable
data object NullResponse : RpcResponse

/** A lower level client that directly executes transactions with the server
 * through direct requests. */
class RpcClient(
    val httpClient: HttpClient = createSimpleHttpClient(),
    val transmissionUrl: Url = defaultTransmissionUrl,
    username: String? = null,
    password: String? = null,
    val logger: KLogger = rpcClientLogger,
    /** The json used to serialize content. encodeDefaults should be `false`; setting it to `true`
     * will cause numerous errors */
    val json: Json = Json
) : CoroutineScope by httpClient {
    internal var csrfToken: String = ""
    private var authToken: String? = null

    init {
        if (username != null && password != null) {
            require(':' !in username){
                "Username can't contain `:`, can't be encoded for basic authentication!"
            }
            val encoded = "$username:$password".encodeBase64()
            authToken = "Basic $encoded"
        }
        logger.info { "rpc client initialized, url = $transmissionUrl" }
    }

    // see chapter 2.1
    // actual data we send to the server
    @Serializable
    data class RpcRequestData(
        val method: String,
        val arguments: RpcRequest<*>? = null,
        val tag: Int? = null
    )

    internal suspend inline fun <reified T : RpcResponse> request(request: RpcRequest<T>): T {
        val rpcRequestData = RpcRequestData(
            method = request.method,
            arguments = request.takeIf { !isObject() }
        )
        var response = sendRpcRequestData(rpcRequestData)
        // see chapter 2.3.1
        when (response.status) {
            HttpStatusCode.Conflict -> { // perfectly valid
                csrfToken = response.headers[CSRF_HEADER]
                    ?: error("response 409 but no `$CSRF_HEADER` in server response!")
                logger.debug { "response 409; updating csrf token (new = $csrfToken), resending" }
                response = sendRpcRequestData(rpcRequestData)
            }

            HttpStatusCode.Unauthorized -> throw TransmissionAuthException(
                if (authToken == null)
                    "auth required; add a username & password to your client"
                else "incorrect username & password for auth!"
            )

            HttpStatusCode.Forbidden -> throw TransmissionAuthException("ip whitelist blocks this ip; disable ip or add your ip to whitelist (Preferences > Remote)")
        }
        val responseText = response.bodyAsText()
        return convertToResponse(responseText)
    }

    // parses the json string and returns the actual rpc response object that the caller was expecting
    // or `null` if the action was not successful
    internal inline fun <reified T : RpcResponse> convertToResponse(responseJsonStr: String): T {
        // see chapter 2.2
        val jsonElement = json.parseToJsonElement(responseJsonStr) as? JsonObject
            ?: error("server response is not an object? $responseJsonStr")
        val result = jsonElement["result"]?.jsonPrimitive?.content
            ?: error("server response has no `result` property: $responseJsonStr")
        if (result != "success") {
            throw TransmissionException("query failed with result: $result")
        }
        val response = jsonElement["arguments"]?.jsonObject
            ?: JsonObject(emptyMap())
        // if no arguments, we should return a null response
        return json.decodeFromJsonElement<T>(response)
    }

    // send the rpc request data to the endpoint
    internal suspend fun sendRpcRequestData(requestData: RpcRequestData): HttpResponse {
        val jsonStr = json.encodeToString(requestData)
        return httpClient.post(transmissionUrl) {
            header(CSRF_HEADER, csrfToken)
            if (authToken != null) {
                header(HttpHeaders.Authorization, authToken)
            }
            setBody(TextContent(jsonStr, ContentType.Application.Json))
        }
    }

    // versioning (chapter 5) is also implemented here

}

open class TransmissionException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)
class TransmissionAuthException(msg: String, cause: Throwable? = null) : TransmissionException(msg, cause)