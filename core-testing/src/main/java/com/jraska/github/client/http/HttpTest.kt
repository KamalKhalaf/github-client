package com.jraska.github.client.http

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.ExternalResource
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object HttpTest {
  private val DEFAULT_BASE_URL = "https://api.github.com".toHttpUrl()

  fun retrofit(baseUrl: HttpUrl = DEFAULT_BASE_URL): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(
        OkHttpClient.Builder()
          .also { if (baseUrl == DEFAULT_BASE_URL) it.addInterceptor(MockWebServerInterceptor) } // TODO: 30/4/21 Hack - unification of network mocking needed
          .addInterceptor(HttpLoggingInterceptor { println(it) }.apply {
            level = HttpLoggingInterceptor.Level.BASIC
          }).build()
      )
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
      .build()
  }
}

fun MockWebServer.enqueue(path: String) {
  this.enqueue(MockResponse().setBody(json(path)))
}

fun MockWebServer.onUrlPartReturn(urlPart: String, jsonPath: String) {
  ensureMapDispatcher()

  (dispatcher as MapDispatcher).onUrlPartReturn(urlPart, jsonPath)
}

fun MockWebServer.onUrlReturn(urlRegex: Regex, jsonPath: String) {
  ensureMapDispatcher()

  (dispatcher as MapDispatcher).onUrlReturn(urlRegex, jsonPath)
}

private fun MockWebServer.ensureMapDispatcher() {
  if (dispatcher !is MapDispatcher) {
    dispatcher = MapDispatcher()
  }
}

internal fun json(path: String): String {
  val uri = HttpTest.javaClass.classLoader.getResource(path)
  val file = File(uri?.path!!)
  return String(file.readBytes())
}

object MockWebServerInterceptor : Interceptor {
  var mockWebServer: MockWebServer? = null

  override fun intercept(chain: Interceptor.Chain): Response {
    val webServer = mockWebServer ?: throw UnsupportedOperationException("You are trying to do network requests in tests you naughty developer!")

    val newRequest = chain.request().newBuilder().url(webServer.url(chain.request().url.encodedPath)).build()
    return chain.proceed(newRequest)
  }
}

class MockWebServerInterceptorRule(private val mockWebServer: MockWebServer) : ExternalResource() {
  override fun before() {
    MockWebServerInterceptor.mockWebServer = mockWebServer
  }

  override fun after() {
    MockWebServerInterceptor.mockWebServer = null
  }
}

