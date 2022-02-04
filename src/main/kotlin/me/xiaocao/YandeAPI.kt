package me.xiaocao

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.xiaocao.model.Post

import org.jsoup.Jsoup
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class CustomX509TrustManager : X509TrustManager {
    override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOf()

    override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}

    override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
}


object YandeAPI {
    private const val targetIP = "198.98.54.92"

    private const val baseUrl = "https://$targetIP"

    private val httpClient: HttpClient = HttpClient(OkHttp) {
        engine {
            config {
                //忽略SSL证书(X509)错误
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, arrayOf(CustomX509TrustManager()), SecureRandom())
                sslSocketFactory(sslContext.socketFactory, CustomX509TrustManager())
                //忽略域名校验
                hostnameVerifier { _, _ -> true }
            }
        }


        defaultRequest {
            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:97.0) Gecko/20100101 Firefox/97.0")
        }
    }

    suspend fun getPosts(page: Int): List<Post> {
        val response = httpClient.get {
            url("$baseUrl/post")
            parameter("page", page)
            header("Host", "yande.re")
        }

        val html = response.body<String>()

        val document = Jsoup.parse(html)

        val ulTag = document.selectFirst(" ul#post-list-posts")!!

        //li标签
        return ulTag.children().map {
            val tagId = it.id()

            //img
            val previewTag = it.getElementsByClass("preview")

            //span
            val directlinkResTag = it.getElementsByClass("directlink-res")

            val directlinkTag = it.getElementsByClass("directlink largeimg")

            Post(
                id = tagId.replace("p", "").toInt(),
                previewUrl = previewTag.attr("src"),
                resolution = directlinkResTag.text(),
                originalUrl = directlinkTag.attr("href")
            )
        }
    }

    suspend fun download(url: String): ByteArray {
        val response = httpClient.get(url.replace("files.yande.re", targetIP)) {
            header("Host", "files.yande.re")
        }
        return response.readBytes()
    }

}