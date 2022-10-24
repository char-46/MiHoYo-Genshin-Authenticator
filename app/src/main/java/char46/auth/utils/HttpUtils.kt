package char46.auth.utils

import android.graphics.BitmapFactory
import android.os.Build
import android.webkit.WebSettings
import char46.auth.Application.Companion.context
import char46.auth.activities.main.currentAccount
import char46.auth.data.MiAccount
import okhttp3.CacheControl
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer

object OkClients {

    private val UA_STRING = buildString {
        append(WebSettings.getDefaultUserAgent(context))
        append(" miHoYoBBS/$BBS_VERSION")
    }

    val NORMAL by lazyOkClient {}

    val WEB by lazyOkClient {
        addInterceptor { chain ->
            val req = chain.request().newBuilder().apply {
                (currentAccount as MiAccount).run {
                    addHeader("Cookie", "ltuid=${uid};ltoken=${lToken}")
                }
                addHeader("User-Agent", UA_STRING)
            }.build()
            chain.proceed(req)
        }
    }

    val SAPI by lazyOkClient {
        addInterceptor { chain ->
            val req = chain.request().newBuilder().apply {
                (currentAccount as MiAccount).run {
                    addHeader(
                        "Cookie", "stuid=${uid};stoken=${sToken};ltuid=${uid};ltoken=${lToken}"
                    )
                }
                addDSHeader()
                addRpcHeader()
            }.build()
            chain.proceed(req)
        }
    }

    val NO_REDIRECT by lazyOkClient {
        followRedirects(false)
        followSslRedirects(false)
    }

    private fun lazyOkClient(block: OkHttpClient.Builder.() -> Unit) = lazy {
        buildOkClient(block)
    }
}

fun OkHttpClient.Builder.addHttpLogger() = apply {
    val i = HttpLoggingInterceptor(Log.okLogger)
    i.level = HttpLoggingInterceptor.Level.BODY
    addInterceptor(i)
}

fun Request.execute(client: OkHttpClient = OkClients.NORMAL) = client.newCall(this).execute()

fun RequestBody.string() = Buffer().use { buf ->
    writeTo(buf)
    buf.readUtf8()
}

fun String.asJsonBody() = toRequestBody("application/json".toMediaType())

fun String.asUrlEncodedBody() = toRequestBody("application/x-www-form-urlencoded".toMediaType())

fun FormBody.Builder.add(name: String, value: Any) = add(name, value.toString())

fun FormBody.Builder.addTimestamp(key: String = "t") = apply { add(key, currentTimeMills) }

fun FormBody.Builder.addCaptchaData(data: CaptchaData) = apply {
    add("mmt_key", data.mmtKey)
    add("geetest_seccode", data.secCode)
    add("geetest_validate", data.validate)
    add("geetest_challenge", data.challenge)
}

fun ResponseBody.bitmap() = BitmapFactory.decodeStream(byteStream())!!

/**
 * Reference: [GitHub](https://git.io/JDgUj)
 * **/
const val BBS_SALT = "xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs"
const val BBS_VERSION = "2.11.1"
const val BBS_CTYPE = "5"
private val nch = CacheControl.Builder().noCache().build()

fun Request.Builder.noCache() = apply { cacheControl(nch) }

fun Request.Builder.addRpcHeader() = apply {
    addHeader("X-Rpc-Channel", "miyousheluodi")
    addHeader("X-Rpc-Device_id", deviceId)
    addHeader("X-Rpc-Client_type", BBS_CTYPE)
    addHeader("X-Rpc-App_version", BBS_VERSION)
    addHeader("X-Rpc-Sys_version", Build.VERSION.RELEASE)
    addHeader("X-Rpc-Device_name", "${Build.MANUFACTURER} ${Build.MODEL}")
    addHeader("X-Rpc-Device_model", Build.MODEL)
}

fun Request.Builder.addDSHeader() = apply {
    val r = build()
    val u = r.url.toString()
    r.body.let {
        if (it != null) {
            addHeader("Ds", createDynamicSecret(u, it.string()))
        } else {
            addHeader("Ds", createDynamicSecret(u))
        }
    }
}

val Response.notNullBody get() = checkNotNull(body)

//fun getText(
//    url: String,
//    client: OkHttpClient = OkClients.NORMAL,
//    header: Map<String, String> = emptyMap(),
//    postBody: RequestBody? = null
//) = buildHttpRequest {
//    url(url)
//    noCache()
//    addHeader("Connection", "close")
//    header.forEach { (k, v) ->
//        addHeader(k, v)
//    }
//    postBody?.let { post(postBody) }
//}.execute(client).notNullBody.string()

fun getText(
    url: String,
    client: OkHttpClient = OkClients.NORMAL,
    header: Map<String, String> = emptyMap(),
    postBody: RequestBody? = null
): String {
    val txt = buildHttpRequest {
        url(url)
        noCache()
        addHeader("Connection", "close")
        header.forEach { (k, v) ->
            addHeader(k, v)
        }
        postBody?.let { post(postBody) }
    }.execute(client).notNullBody.string()
    return txt
}

fun Request.Builder.postFormBody(block: FormBody.Builder.() -> Unit) = post(buildFormBody(block))

class CookieBuilderScope {

    internal val cMap: HashMap<String, String> = hashMapOf()

    fun add(key: String, value: String) {
        cMap[key] = value
    }

}

fun buildCookieString(block: CookieBuilderScope.() -> Unit) =
    CookieBuilderScope().apply(block).cMap.map { (k, v) -> "$k=$v" }.joinToString("; ")

fun cookieStringToMap(s: String) = s.split(";").map { it.trim() }.associate { ts ->
    ts.split("=").let { it[0] to it[1] }
}

fun buildFormBody(block: FormBody.Builder.() -> Unit) = FormBody.Builder().apply(block).build()

fun buildHttpRequest(block: Request.Builder.() -> Unit) = Request.Builder().apply(block).build()

fun buildOkClient(block: OkHttpClient.Builder.() -> Unit) =
    OkHttpClient.Builder().addHttpLogger().apply(block).build()

//fun createDynamicSecret(url: String, data: String = ""): String {
//    var q = ""
//    url.split("?").getOrNull(1)?.let {
//        q = it.split("&").sorted().joinToString("&")
//    }
//    val t = currentTimeSeconds
//    val r = Random.nextString(6)
//    val chk = "salt=$BBS_SALT&t=$t&r=$r&b=$data&q=$q".digest("MD5")
//    return "$t,$r,$chk"
//}
/**
 * https://github.com/Azure99/GenshinPlayerQuery/blob/main/src/Core/GenshinAPI.cs#L121-L137
 * Credits: lulu666lulu, https://github.com/Azure99/GenshinPlayerQuery/issues/20
 */
fun createDynamicSecret(url: String, body: String = ""): String {
    var query: String = ""
    val urlPart = url.split("?")
    if (urlPart.size == 2) {
        val partment = urlPart[1].split("&").sorted()
        query = partment.joinToString("&")
    }
    val time = currentTimeSeconds
    val random = createRandomString(6)
    val check = "salt=$BBS_SALT&t=$time&r=$random&b=$body&q=$query".digest("MD5")

    return "${time},${random},${check}"
}

fun createRandomString(length: Int): String {
    val randomStringTemplate = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    var string = ""
    for (i in 0 until length) {
        string += randomStringTemplate.random()
    }
    return string
}

/**
import time, hashlib, random

def ds(url, data=''):
salt = 'xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs'
(q := url.split('?')[1].split('&')).sort()
q = '&'.join(q)
r = rString(6)
t = int(time.time())
chk = f'salt={salt}&t={t}r={r}&b={data}&q={q}'
chk = hashlib.md5(chk.encode()).hexdigest()
return f'{t},{r},{chk}'

def rString(length):
chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
return ''.join(random.sample(chars, length))

 */