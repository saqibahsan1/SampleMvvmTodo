package com.example.network.interceptor

import android.annotation.SuppressLint
import com.example.network.AppLogger
import com.example.network.EMPTY_STRING
import com.example.network.GsonHelper
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Our own logging interceptor
 *
 *
 * Main benefit of using this is that this appends new lines to the same log entry rather than
 * creating a new one
 *
 *
 * It also directly uses [AppLogger.d] so no chance of miss-configured logging level in a third
 * party logging tool.
 */

interface HttpLoggingInterceptor : Interceptor

class CustomHttpLoggingInterceptor @Inject constructor() : HttpLoggingInterceptor {
    private val gson = GsonHelper.prettyGson
    private val jp = JsonParser()

    // https://developer.android.com/studio/write/java8-support.html
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (AppLogger.isDebugEnabled.not()) {
            return chain.proceed(request)
        }
        var sb = StringBuilder().append(NL)
        val requestBody = request.body
        val hasRequestBody = requestBody != null
        val connection = chain.connection()
        val protocol =
            connection?.protocol() ?: Protocol.HTTP_1_1
        var requestStartMessage =
            "--> " + request.method + ' ' + request.url + ' ' + protocol
        if (hasRequestBody) {
            requestStartMessage += " (" + requestBody!!.contentLength() + BYTE_BODY_MESSAGE
        }
        sb.append(requestStartMessage).append(NL)
        if (hasRequestBody) {
            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if (requestBody!!.contentType() != null) {
                sb.append("Content-Type: " + requestBody.contentType())
                    .append(NL)
            }
            if (requestBody.contentLength() != -1L) {
                sb.append("Content-Length: " + requestBody.contentLength())
                    .append(NL)
            }
        }
        var headers = request.headers
        run {
            var i = 0
            val count = headers.size
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(
                        name, ignoreCase = true
                    )
                ) {
                    sb.append(name + ": " + headers.value(i))
                        .append(NL)
                }
                i++
            }
        }
        if (!hasRequestBody) {
            sb.append(END + request.method)
                .append(NL)
        } else if (bodyHasUnknownEncoding(request.headers)) {
            sb.append(END + request.method + " (encoded body omitted)")
                .append(NL)
        } else {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)
            var charset = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            sb.append(NL)
            if (isPlaintext(buffer)) {
                getPrettyJsonBody(sb, buffer, charset)
                sb.append(
                    END + request.method + " (" + requestBody.contentLength() +
                        BYTE_BODY_MESSAGE
                ).append(NL)
            } else {
                sb.append(
                    END + request.method + " (binary " + requestBody.contentLength() +
                        "-byte body omitted)"
                ).append(NL)
            }
        }
        AppLogger.networkLog(sb.toString())
        sb = StringBuilder().append(NL)
        val startNs = System.nanoTime()
        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            sb.append("<-- HTTP FAILED: $e").append(NL)
            throw e
        }
        val tookMs =
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()
        val bodySize =
            if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        sb.append(
            "<-- " + response.code + ' ' + response.message + ' ' + request.method + ' ' +
                response.request.url + " (" + tookMs + "ms" + ", " + bodySize +
                " body" + ')'
        ).append(NL)
        headers = response.headers
        var i = 0
        val count = headers.size
        while (i < count) {
            sb.append(headers.name(i) + ": " + headers.value(i))
                .append(NL)
            i++
        }
        if (!response.promisesBody()) {
            sb.append("<-- END HTTP").append(NL)
        } else if (bodyHasUnknownEncoding(response.headers)) {
            sb.append("<-- END HTTP (encoded body omitted)")
                .append(NL)
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer()
            try {
                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }
                var charset = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = try {
                        contentType.charset(UTF8)
                    } catch (e: UnsupportedCharsetException) {
                        sb.append("").append(NL)
                        sb.append("Couldn't decode the response body; charset is likely malformed.")
                            .append(NL)
                        sb.append("<-- END HTTP").append(NL)
                        AppLogger.networkLog(sb.toString())
                        return response
                    }
                }
                if (!isPlaintext(buffer)) {
                    sb.append("").append(NL)
                    sb.append("<-- END HTTP (binary " + buffer.size + "-byte body omitted)")
                        .append(NL)
                    AppLogger.networkLog(sb.toString())
                    return response
                }
                if (contentLength != 0L) {
                    sb.append("").append(NL)
                    getPrettyJsonBody(sb, buffer, charset)
                }
                if (gzippedLength != null) {
                    sb.append(
                        "<-- END HTTP (" + buffer.size + "-byte, " + gzippedLength +
                            "-gzipped-byte body)"
                    ).append(NL)
                } else {
                    sb.append("<-- END HTTP (" + buffer.size + BYTE_BODY_MESSAGE)
                        .append(NL)
                }
            } finally {
                buffer.close()
            }
        }
        AppLogger.networkLog(sb.toString())
        return response
    }

    private fun getPrettyJsonBody(
        sb: StringBuilder,
        buffer: Buffer,
        charset: Charset?,
    ) {
        var bodyString: String = EMPTY_STRING
        try {
            bodyString = buffer.clone().readString(charset!!)
            val je = jp.parse(bodyString)
            val prettyJsonString = gson.toJson(je)
            sb.append(prettyJsonString).append(NL)
        } // intentionally not log again in middle of logging or rethrow this exception
        catch (e: JsonParseException) {
            sb.append(bodyString).append(NL)
        } catch (e: OutOfMemoryError) {
            sb.append(bodyString).append(NL)
        }
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !contentEncoding.equals(
            "identity",
            ignoreCase = true
        ) && !contentEncoding.equals("gzip", ignoreCase = true)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
        private const val NL = '\n'
        const val BYTE_BODY_MESSAGE = "-byte body)"
        const val END = "--> END "

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small
         * sample of code points to detect unicode control characters commonly used in binary file
         * signatures.
         */
        @SuppressLint("NewApi") // https://developer.android.com/studio/write/java8-support.html
        private fun isPlaintext(buffer: Buffer): Boolean {
            try {
                Buffer().use { prefix ->
                    val byteCount = if (buffer.size < 64) buffer.size else 64
                    buffer.copyTo(prefix, 0, byteCount)
                    for (i in 0..15) {
                        if (prefix.exhausted()) {
                            break
                        }
                        val codePoint = prefix.readUtf8CodePoint()
                        if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                            return false
                        }
                    }
                    return true
                }
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }
        }
    }
}
